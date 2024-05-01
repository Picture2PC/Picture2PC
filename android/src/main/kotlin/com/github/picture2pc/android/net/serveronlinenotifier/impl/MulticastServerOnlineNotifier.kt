package com.github.picture2pc.android.net.serveronlinenotifier.impl

import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.android.net.serveronlinenotifier.ServerOnlineNotifier
import com.github.picture2pc.common.net2.NetworkPayloadTransceiver
import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.impl.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net2.impl.tcp.TcpPayloadTransceiver
import com.github.picture2pc.common.net2.payloads.MulticastPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.CoroutineContext

class MulticastServerOnlineNotifier(
    private val multicastPaylaodTransceiver: MulticastPayloadTransceiver,
    private val tcpPayloadTransceiver: TcpPayloadTransceiver,
    override val coroutineContext: CoroutineContext,
    override val serverPreferencesRepository: ServerPreferencesRepository
) : ServerOnlineNotifier, CoroutineScope {
    private val serverName = serverPreferencesRepository.name.stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = "<LOADING>"
    )

    private val serverConnectable = serverPreferencesRepository.connectable.stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    init {
        multicastPaylaodTransceiver.receivedPayloads.onEach { payload ->
            (payload as? MulticastPayload.ListPeers)?.let {
                if (serverConnectable.value) {
                    emitServerOnline(serverName.value, it.sourcePeer)
                    tcpPayloadTransceiver.connect(it.sourcePeer)

                }
            }
        }.launchIn(this)
    }

    private suspend fun emitServerOnline(serverName: String, peer: Peer = Peer.any()) {
        NetworkPayloadTransceiver.name = serverName
        multicastPaylaodTransceiver.sendPayload(
            MulticastPayload.PeerTcpOnline(
                tcpPayloadTransceiver.inetSocketAddress.port,
                peer
            )
        )
    }
}