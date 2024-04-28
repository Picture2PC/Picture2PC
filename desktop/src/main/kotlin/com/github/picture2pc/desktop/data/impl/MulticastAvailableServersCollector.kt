package com.github.picture2pc.desktop.data.impl

import com.github.picture2pc.common.net2.impl.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net2.payloads.ListPeers
import com.github.picture2pc.common.net2.payloads.PeerOnline
import com.github.picture2pc.desktop.data.AvailableServersCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MulticastAvailableServersCollector(
    private val multicastPaylaodTransceiver: MulticastPayloadTransceiver,
    override val coroutineContext: CoroutineContext,
) : AvailableServersCollector, CoroutineScope {
    override val availableServers = MutableSharedFlow<AvailableServersCollector.Server>()

    init {
        multicastPaylaodTransceiver.receivedPayloads
            .onEach { payload ->
                (payload as? PeerOnline)?.let { peerOnline ->
                    val server = AvailableServersCollector.Server(payload.sourcePeer.name, "a")
                    availableServers.emit(server)
                }
            }
            .launchIn(this)
    }

    override fun requestServers() {
        launch {
            multicastPaylaodTransceiver.sendPayload(ListPeers())
        }
    }

}
