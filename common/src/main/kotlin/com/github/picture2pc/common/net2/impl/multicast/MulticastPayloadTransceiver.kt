package com.github.picture2pc.common.net2.impl.multicast


import com.github.picture2pc.common.net2.NetworkPayloadTransceiver
import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.coroutines.CoroutineContext

class MulticastPayloadTransceiver internal constructor(
    override val coroutineContext: CoroutineContext
) : CoroutineScope, KoinComponent, NetworkPayloadTransceiver() {
    private var multicastSocket: SimpleMulticastSocket = get()

    init {
        launch {
            multicastSocket.updateNetworkInterface()
            while (isActive) {
                multicastSocket.receivePacket()?.let { packet ->
                    receivedPayload(packet)
                }
            }
        }
    }

    override suspend fun _sendPayload(payload: Payload): Boolean {
        return multicastSocket.sendMessage(payload.asInputStream())
    }
}
