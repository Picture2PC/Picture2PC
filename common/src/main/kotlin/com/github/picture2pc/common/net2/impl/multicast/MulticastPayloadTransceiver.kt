package com.github.picture2pc.common.net2.impl.multicast


import com.github.picture2pc.common.net2.NetworkPayloadTransceiver
import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.net.InetSocketAddress
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalSerializationApi::class)
class MulticastPayloadTransceiver internal constructor(
    inetSocketAddress: InetSocketAddress,
    override val coroutineContext: CoroutineContext
) : CoroutineScope, NetworkPayloadTransceiver {
    private val multicastSocket = SimpleMulticastSocket(inetSocketAddress)

    init {
        launch {
            while (isActive) {
                multicastSocket.receivePacket()?.let { packet ->
                    val payload = Json.decodeFromStream<Payload>(packet.content)
                    if (payload.targetPeer == NetworkPayloadTransceiver.getSelf() || payload.targetPeer.isAny) {
                        _receivedPayloads.emit(payload)
                    }
                }
            }
        }
    }

    private val _receivedPayloads: MutableSharedFlow<Payload> = MutableSharedFlow()
    override val receivedPayloads: SharedFlow<Payload> = _receivedPayloads.asSharedFlow()
    override fun sendPayload(payload: Payload) {
        multicastSocket.sendMessage(payload.asInputStream())
    }

}
