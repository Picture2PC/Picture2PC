package com.github.picture2pc.desktop.data.availableserverscollector.impl

import com.github.picture2pc.common.net2.impl.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net2.impl.tcp.TcpPayloadTransceiver
import com.github.picture2pc.common.net2.payloads.MulticastPayload
import com.github.picture2pc.desktop.data.availableserverscollector.AvailableServersCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MulticastAvailableServersCollector(
    private val multicastPaylaodTransceiver: MulticastPayloadTransceiver,
    private val tcpPayloadTransceiver: TcpPayloadTransceiver,
    override val coroutineContext: CoroutineContext,
) : AvailableServersCollector, CoroutineScope {
    override val availableServers = MutableSharedFlow<AvailableServersCollector.Server>()

    init {
        multicastPaylaodTransceiver.receivedPayloads
            .onEach { payload ->
                (payload as? MulticastPayload.PeerTcpOnline)?.let { peerOnline ->
                    val server = AvailableServersCollector.Server(
                        payload.sourcePeer.name,
                        payload.receivedFromInetSocketAddress?.hostString ?: "unknown",
                    )
                    tcpPayloadTransceiver.connect(
                        payload.sourcePeer,
                        payload.tcpServerSocketAddress
                    )
                    availableServers.emit(server)
                }
            }
            .launchIn(this)
    }

    override fun requestServers() {
        launch {
            multicastPaylaodTransceiver.sendPayload(MulticastPayload.ListPeers())
        }
    }
}   