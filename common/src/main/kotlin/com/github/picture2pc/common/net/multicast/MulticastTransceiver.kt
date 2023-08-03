package com.github.picture2pc.common.net.multicast

import kotlin.reflect.KClass

private typealias PayloadReceivedCallback<PayloadType> = (payload: PayloadType, packet: ReceivedMulticastPacket) -> Unit

private class Subscription<PayloadType : MulticastPayload>(
    private val paylaodClass: KClass<PayloadType>,
    private val callback: PayloadReceivedCallback<PayloadType>
) {
    fun invokeCallbackIfPayloadMatches(payload: MulticastPayload, packet: ReceivedMulticastPacket) {
        if (paylaodClass.isInstance(payload)) {
            @Suppress("UNCHECKED_CAST")
            callback.invoke(payload as PayloadType, packet)
        }
    }
}

class MulticastTransceiver internal constructor(private val socket: SimpleMulticastSocket) {
    private val subscriptions = mutableListOf<Subscription<*>>()
    private var awationLoopActive = false

    fun sendPayload(payload: MulticastPayload) {
        socket.sendMessage(payload.stringRepresentation)
    }

    fun <PayloadType : MulticastPayload> subscribeToPayload(
        payloadClass: KClass<PayloadType>,
        callback: PayloadReceivedCallback<PayloadType>
    ) {
        subscriptions += Subscription(payloadClass, callback)
    }

    inline fun <reified PayloadType : MulticastPayload> subscribeToPayload(
        noinline callback: PayloadReceivedCallback<PayloadType>
    ) {
        subscribeToPayload(PayloadType::class, callback)
    }


    fun awaitNextMessage(timeoutMs: Int? = null): MulticastPayload? {
        val packet = socket.recievePacket(timeoutMs) ?: return null
        val payload = MulticastPayloads.createPayloadFromString(packet.content) ?: return null

        subscriptions.forEach { it.invokeCallbackIfPayloadMatches(payload, packet) }
        return payload
    }

    fun runAwationLoop() {
        awationLoopActive = true
        while (awationLoopActive) awaitNextMessage(50)
    }

    fun stopAwationLoop() {
        awationLoopActive = false
    }

}

