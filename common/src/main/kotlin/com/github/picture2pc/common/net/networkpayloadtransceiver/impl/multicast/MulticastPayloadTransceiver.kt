package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast


import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.extentions.getDefaultNetworkInterfaces
import com.github.picture2pc.common.net.networkpayloadtransceiver.NetworkPayloadTransceiver
import com.sun.org.apache.xpath.internal.operations.Mult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
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
        multicastSockets.put(networkInterface, multicastSocket)
        scope.launch {
            while (true) {
                if (!multicastSocket.isAvailable){
                    stopSingleSocket(networkInterface)
                    return@launch
                }
                println(multicastSocket.receivePayload())
                receivedPayload(multicastSocket.receivePayload() ?: continue)
            }
        }
    }

    private fun stopSingleSocket(networkInterface: NetworkInterface){
        multicastSockets.remove(networkInterface)?.close()
    }

    override suspend fun _sendPayload(payload: Payload): Boolean {
        return multicastSockets.values.map{it.sendMessage(payload)}.any()
    }
}
