package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast


import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.extentions.getDefaultNetworkInterfaces
import com.github.picture2pc.common.net.networkpayloadtransceiver.NetworkPayloadTransceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.net.NetworkInterface

class MulticastPayloadTransceiver(
    private val scope: CoroutineScope,
) : KoinComponent, NetworkPayloadTransceiver() {

    private val multicastSockets: MutableMap<NetworkInterface, SimpleMulticastSocket> = mutableMapOf()

    override val available: Boolean
        get() = multicastSockets.values.any { it.isAvailable }

    override suspend fun start() {
        updateMulticastSockets()

        scope.launch {
            while (isActive){
                updateMulticastSockets()
                delay(MulticastConstants.UPDATE_INTERFACE_DELAY)
            }
        }
    }

    private suspend fun updateMulticastSockets(){
        while (kotlin.runCatching {
                val interfaces = getDefaultNetworkInterfaces().toSet()
                val newInterfaces = interfaces.minus(multicastSockets.keys)
                val removeInterfaces = multicastSockets.keys.minus(interfaces)
                println(multicastSockets)
                newInterfaces.forEach {
                    startSingleSocket(it)
                }
                removeInterfaces.forEach {
                    stopSingleSocket(it)
                }
            }.isFailure) {
            delay(MulticastConstants.RETRY_DELAY)
        }
    }

    private suspend fun startSingleSocket(networkInterface: NetworkInterface){
        val multicastSocket: SimpleMulticastSocket = get()
        multicastSocket.start(networkInterface)
        multicastSockets[networkInterface] = multicastSocket
        scope.launch {
            while (isActive) {
                if (!multicastSocket.isAvailable){
                    stopSingleSocket(networkInterface)
                    return@launch
                }
                println(multicastSocket.receivePayload())
                val payload = multicastSocket.receivePayload() ?: continue
                launch {
                    withTimeoutOrNull(2000) {
                        sendPayloadExcluding(payload, multicastSocket)
                    }
                }
                receivedPayload(payload)
            }
        }
    }

    private fun stopSingleSocket(networkInterface: NetworkInterface){
        multicastSockets.remove(networkInterface)?.close()
    }

    private suspend fun sendPayloadExcluding(
        payload: Payload,
        simpleMulticastSocket: SimpleMulticastSocket? = null
    ): Boolean {
        return multicastSockets.values.filter { it != simpleMulticastSocket }
            .map { it.sendMessage(payload) }.any()
    }

    override suspend fun _sendPayload(payload: Payload): Boolean {
        return sendPayloadExcluding(payload)
    }
}
