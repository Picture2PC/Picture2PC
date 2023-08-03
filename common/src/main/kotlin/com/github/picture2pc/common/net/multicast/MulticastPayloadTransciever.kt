package com.github.picture2pc.common.net.multicast

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MulticastPayloadTransciever internal constructor(
    private val socket: SimpleMulticastSocket,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    val outgoingPayloads = MutableSharedFlow<MulticastPayload>()
    init {
        outgoingPayloads
            .onEach { socket.sendMessage(it.stringRepresentation) }
            .launchIn(this)
    }

    private val _incomingPayloads = MutableSharedFlow<ReceivedMulticastPayload>()
    val incomingPayloads = _incomingPayloads.asSharedFlow()
    init {
        launch {
            while (isActive) {
                val packet = socket.recievePacket(timeoutMs = 50) ?: continue
                val payload = MulticastPayloads.createPayloadFromString(packet.content) ?: continue
                val receivedMulticastPayload = ReceivedMulticastPayload(payload, packet.address)
                launch { _incomingPayloads.emit(receivedMulticastPayload) }
            }
        }
    }
}
