package com.github.picture2pc.desktop.data.availableserverscollector.impl

import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.impl.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net2.impl.tcp.ClientState
import com.github.picture2pc.common.net2.impl.tcp.TcpPayloadTransceiver
import com.github.picture2pc.common.net2.payloads.MulticastPayload
import com.github.picture2pc.desktop.data.availableserverscollector.AvailableServersCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MulticastAvailableServersCollector(
    private val multicastPayloadTransceiver: MulticastPayloadTransceiver,
    private val tcpPayloadTransceiver: TcpPayloadTransceiver,
    override val coroutineContext: CoroutineContext,
) : AvailableServersCollector, CoroutineScope {
    override val availableServers
        get() = tcpPayloadTransceiver.connectedPeers

    init {
        multicastPayloadTransceiver.receivedPayloads
            .onEach { payload ->
                (payload as? MulticastPayload.PeerTcpOnline)?.let { peerOnline ->
                    tcpPayloadTransceiver.connect(
                        payload.sourcePeer,
                        payload.tcpServerSocketAddress
                    )
                }
            }
            .launchIn(this)
    }

    override fun requestServers() {
        launch {
            multicastPayloadTransceiver.sendPayload(MulticastPayload.ListPeers())
        }
    }

    override fun getClientState(peer: Peer): StateFlow<ClientState>? {
        return tcpPayloadTransceiver.getPeerStateAsStateFlow(peer)
    }
}   