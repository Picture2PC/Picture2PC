package com.github.picture2pc.common.net.multicast

import com.github.picture2pc.common.net.common.NetworkDataPayload
import com.github.picture2pc.common.net.common.NetworkDataPayloadEventHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalSerializationApi::class)
class MulticastPayloadTransceiver internal constructor(
    private val socket: SimpleMulticastSocket,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    init {
        NetworkDataPayloadEventHandler.outgoingPayloads
            .onEach { socket.sendMessage(it) }
            .launchIn(this)

        launch {
            while (isActive) {
                val packet = socket.recievePacket(timeoutMs = 50) ?: continue
                val payload = Json.decodeFromStream<NetworkDataPayload>(packet.content)
                payload.newEvent(payload, packet.address);
            }
        }
    }
}
