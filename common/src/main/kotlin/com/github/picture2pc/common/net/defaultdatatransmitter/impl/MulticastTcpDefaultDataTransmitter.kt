package com.github.picture2pc.common.net.defaultdatatransmitter.impl

import com.github.picture2pc.common.data.preferences.PreferencesRepository
import com.github.picture2pc.common.net.data.payload.MulticastPayload
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.defaultdatatransmitter.DefaultDataTransmitter
import com.github.picture2pc.common.net.defaultdatatransmitter.DefaultDevice
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
    private val serverPreferences: PreferencesRepository,
    private val backgroundScope: CoroutineScope,
) : DefaultDataTransmitter {
    private val _connectedDevices: MutableStateFlow<List<DefaultDevice>> =
        MutableStateFlow(emptyList())
    override val connectedDevices: StateFlow<List<DefaultDevice>> = _connectedDevices

    private val _pictures: MutableSharedFlow<TcpPayload.Picture> =
        MutableSharedFlow(extraBufferCapacity = 1)
    override val picture: SharedFlow<TcpPayload.Picture> = _pictures

    companion object {
        const val TIME_BETWEEN_ONLINE_EMIT = 3000L
    }

    private val uuidNameMap = mutableMapOf<String, MutableStateFlow<String>>()
    private val uuidCanReceiveMap = mutableMapOf<String, MutableStateFlow<Boolean>>()
    private val uuidGroupVerifiedMap = mutableMapOf<String, MutableStateFlow<Boolean>>()

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

                    is TcpPayload.GroupVerification -> {
                        val verified = verifyGroupHash(it.groupHash, it.sourcePeer.uuid)
                        tcpPayloadTransceiver.sendPayload(TcpPayload.GroupVerified(verified, it.sourcePeer))
                        if (verified) {
                            uuidGroupVerifiedMap[it.sourcePeer.uuid]?.emit(true)
                        }
                    }

                    is TcpPayload.GroupVerified -> {
                        uuidGroupVerifiedMap[it.sourcePeer.uuid]?.emit(it.verified)
                    }

                    is TcpPayload.Picture -> {
                        // Only accept pictures from group-verified devices
                        if (uuidGroupVerifiedMap[it.sourcePeer.uuid]?.value == true) {
                            _pictures.emit(it)
                        }
                    }

                    else -> {}
                }
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
                ensureDeviceState(it.peer.uuid)
                // Send group verification when a new peer connects
                sendGroupVerification(it.peer)
            }
            _connectedDevices.emit(connected.map {
                DefaultDevice(
                    uuidNameMap[it.peer.uuid]!!,
                    it.clientStateFlow,
                    uuidCanReceiveMap[it.peer.uuid]!!,
                    uuidGroupVerifiedMap[it.peer.uuid]!!
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

    private suspend fun ensureDeviceState(uuid: String) {
        if (!uuidCanReceiveMap.containsKey(uuid)) {
            uuidCanReceiveMap[uuid] = MutableStateFlow(true)
        }
        if (!uuidGroupVerifiedMap.containsKey(uuid)) {
            uuidGroupVerifiedMap[uuid] = MutableStateFlow(false)
        }
    }

    private fun computeGroupHash(groupUuid: String, deviceUuid: String): String {
        val combined = "$groupUuid:$deviceUuid"
        return combined.hashCode().toString()
    }

    private suspend fun sendGroupVerification(peer: Peer) {
        val groupUuid = serverPreferences.groupUuid.value
        val deviceUuid = serverPreferences.deviceUuid.value
        
        if (groupUuid.isEmpty() || deviceUuid.isEmpty()) {
            return
        }
        
        val hash = computeGroupHash(groupUuid, deviceUuid)
        tcpPayloadTransceiver.sendPayload(TcpPayload.GroupVerification(hash, peer))
    }

    private suspend fun verifyGroupHash(hash: String, peerUuid: String): Boolean {
        val groupUuid = serverPreferences.groupUuid.value
        
        if (groupUuid.isEmpty()) {
            return false
        }
        
        val expectedHash = computeGroupHash(groupUuid, peerUuid)
        return hash == expectedHash
    }

    private suspend fun newName(name: String, peer: Peer = Peer.any()) {
        tcpPayloadTransceiver.sendPayload(TcpPayload.NameUpdate(name, peer))
    }

    private suspend fun requestNameTcpPeer(peer: Peer) {
        tcpPayloadTransceiver.sendPayload(TcpPayload.RequestName(peer))
    }

    override suspend fun sendPicture(picturePayload: TcpPayload.Picture) : Boolean {
        // Only send to devices that are group-verified and have receiving enabled
        val targetUuid = if (picturePayload.targetPeer.isAny) null else picturePayload.targetPeer.uuid
        
        if (targetUuid != null) {
            // Sending to specific peer
            val groupVerified = uuidGroupVerifiedMap[targetUuid]?.value ?: false
            val canReceive = uuidCanReceiveMap[targetUuid]?.value ?: false
            
            if (!groupVerified || !canReceive) {
                return false
            }
        }
        
        return tcpPayloadTransceiver.sendPayload(picturePayload)
    }

    override suspend fun setDeviceCanReceive(deviceUuid: String, canReceive: Boolean) {
        uuidCanReceiveMap[deviceUuid]?.emit(canReceive)
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