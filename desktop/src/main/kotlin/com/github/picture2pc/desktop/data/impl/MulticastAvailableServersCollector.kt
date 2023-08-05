package com.github.picture2pc.desktop.data.impl

import com.github.picture2pc.common.net.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net.multicast.MulticastPayloads
import com.github.picture2pc.desktop.data.AvailableServersCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MulticastAvailableServersCollector(
    private val multicastPaylaodTransceiver: MulticastPayloadTransceiver,
    override val coroutineContext: CoroutineContext
) : AvailableServersCollector, CoroutineScope {
    override val availableServers = MutableSharedFlow<AvailableServersCollector.Server>()

    init {
        multicastPaylaodTransceiver.incomingPayloads
            .onEach { (payload, senderAddress) ->
                if (payload is MulticastPayloads.ServerOnline) {
                    val server = AvailableServersCollector.Server(payload.deviceName, senderAddress)
                    availableServers.emit(server)
                }
            }
            .launchIn(this)
    }

    override fun requestServers() {
        launch {
            multicastPaylaodTransceiver.outgoingPayloads.emit(MulticastPayloads.ListServers)
        }
    }

}
