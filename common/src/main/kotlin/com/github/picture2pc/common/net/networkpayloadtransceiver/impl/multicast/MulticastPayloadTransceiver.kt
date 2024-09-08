package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast


import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.networkpayloadtransceiver.NetworkPayloadTransceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent

class MulticastPayloadTransceiver(
    private val scope: CoroutineScope,
    private val multicastSocket: SimpleMulticastSocket
) : KoinComponent, NetworkPayloadTransceiver() {


    override val available: Boolean
        get() = multicastSocket.isAvailable

    override suspend fun start() {
        while (kotlin.runCatching { multicastSocket.start() }.isFailure) {
            delay(MulticastConstants.RETRY_DELAY)
        }

        scope.launch {
            while (isActive) {
                while (isActive) {
                    try {
                        withTimeout(MulticastConstants.RETRY_DELAY) {
                            multicastSocket.start()
                        }
                    } catch (e: Exception) {
                        delay(MulticastConstants.RETRY_DELAY)
                        continue
                    }
                    break
                }
                while (isActive) {
                    if (!multicastSocket.isAvailable)
                        break
                    receivedPayload(multicastSocket.receivePayload() ?: continue)
                }
            }
        }
    }

    override suspend fun _sendPayload(payload: Payload): Boolean {
        return multicastSocket.sendMessage(payload)
    }
}
