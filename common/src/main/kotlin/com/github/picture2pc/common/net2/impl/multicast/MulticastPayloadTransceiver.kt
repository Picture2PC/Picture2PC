package com.github.picture2pc.common.net2.impl.multicast


import com.github.picture2pc.common.net2.NetworkPayloadTransceiver
import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.coroutines.CoroutineContext

class MulticastPayloadTransceiver internal constructor(
    override val coroutineContext: CoroutineContext
) : CoroutineScope, KoinComponent, NetworkPayloadTransceiver() {
    private var multicastSocket: SimpleMulticastSocket? = null

    init {
        launch {
            while (isActive) {
                while (isActive) {
                    try {
                        multicastSocket = get()
                    } catch (e: Exception) {
                        delay(MulticastConstants.RETRY_DELAY)
                        continue
                    }
                    break
                }
                while (isActive) {
                    if (multicastSocket?.isAvailable != true)
                        break
                    receivedPayload(multicastSocket?.receivePacket() ?: continue)
                }
            }
        }
    }

    override suspend fun _sendPayload(payload: Payload): Boolean {
        return multicastSocket?.sendMessage(payload.asInputStream()) ?: false
    }
}
