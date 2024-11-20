package com.github.picture2pc.android.net.datatransmitter.impl

import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.android.net.datatransmitter.DefaultDevice
import com.github.picture2pc.common.net.data.payload.MulticastPayload
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpPayloadTransceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

open class MulticastTcpDefaultDataTransmitter(
    private val multicastPayloadTransceiver: MulticastPayloadTransceiver,
    private val tcpPayloadTransceiver: TcpPayloadTransceiver,
    private val serverPreferences: ServerPreferencesRepository,
    private val backgroundScope: CoroutineScope,
) {
    private val _connectedDevices: MutableStateFlow<List<DefaultDevice>> =
        MutableStateFlow(emptyList())
    val connectedDevices: StateFlow<List<DefaultDevice>> = _connectedDevices

    private val _pictures: MutableSharedFlow<TcpPayload.Picture> = MutableSharedFlow(5, 1)
    val pictures: SharedFlow<TcpPayload.Picture> = _pictures

    companion object {
        const val TIME_BETWEEN_ONLINE_EMIT = 3000L
    }

    private val uuidNameMap = mutableMapOf<String, MutableStateFlow<String>>()

    init {
        backgroundScope.launch {
            tcpPayloadTransceiver.start()
            multicastPayloadTransceiver.start()

            multicastPayloadTransceiver.receivedPayloads.onEach { payload ->
                when (payload) {
                    is MulticastPayload.PeerTcpOnline -> {
                        newUUidName(payload.sourcePeer.uuid, payload.clientName)
                        if (serverPreferences.connectable.value) {
                            backgroundScope.launch {
                                tcpPayloadTransceiver.connect(
                                    payload.sourcePeer,
                                    payload.tcpServerSocketAddress
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }.launchIn(backgroundScope)

            tcpPayloadTransceiver.receivedPayloads.onEach {
                when (it) {
                    is TcpPayload.RequestName -> {
                        newName(serverPreferences.name.value, it.sourcePeer)
                    }

                    is TcpPayload.NameUpdate -> {
                        newUUidName(it.sourcePeer.uuid, it.name)
                    }

                    is TcpPayload.Picture -> {
                        _pictures.emit(it)
                    }

                    else -> {}
                }
            }.launchIn(backgroundScope)
            serverPreferences.name.onEach {
                newName(it)
            }.launchIn(backgroundScope)

            while (isActive) {
                if (serverPreferences.connectable.value) {
                    emitServerOnline(serverPreferences.name.value)
                }
                kotlinx.coroutines.delay(TIME_BETWEEN_ONLINE_EMIT)
            }
        }

        tcpPayloadTransceiver.connectedPeers.onEach { connected ->
            connected.forEach {
                if (!uuidNameMap.containsKey(it.peer.uuid)) {
                    newUUidName(it.peer.uuid, "Unknown")
                    requestNameTcpPeer(it.peer)
                }
            }
            _connectedDevices.emit(connected.map {
                DefaultDevice(
                    uuidNameMap[it.peer.uuid]!!,
                    it.clientStateFlow
                )
            })
        }.launchIn(backgroundScope)
    }

    suspend fun refreshDevices() {
        emitListServers()
    }

    private suspend fun newUUidName(uuid: String, name: String) {
        if (uuidNameMap.containsKey(uuid))
            uuidNameMap[uuid]?.emit(name)
        else
            uuidNameMap[uuid] = MutableStateFlow(name)
    }

    private suspend fun newName(name: String, peer: Peer = Peer.any()) {
        tcpPayloadTransceiver.sendPayload(TcpPayload.NameUpdate(name, peer))
    }

    private suspend fun requestNameTcpPeer(peer: Peer) {
        tcpPayloadTransceiver.sendPayload(TcpPayload.RequestName(peer))
    }

    suspend fun sendPicture(payload: TcpPayload.Picture) {
        tcpPayloadTransceiver.sendPayload(payload)
    }

    private suspend fun emitListServers() {
        multicastPayloadTransceiver.sendPayload(MulticastPayload.ListPeers(serverPreferences.name.value))
    }

    private suspend fun emitServerOnline(serverName: String) {
        multicastPayloadTransceiver.sendPayload(
            MulticastPayload.PeerTcpOnline(
                tcpPayloadTransceiver.inetSocketAddress.port,
                serverName,
                Peer.any()
            )
        )
    }
}