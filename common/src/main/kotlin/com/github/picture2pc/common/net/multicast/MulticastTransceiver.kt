package com.github.picture2pc.common.net.multicast

private typealias PayloadRecievedCallback = () -> Unit

class MulticastTransceiver {
    companion object {
        private const val multicastAddress = "232.242.119.180"
        private const val multicastPort = 42852

        enum class Payload(val content: String) {
            SERVER_ONLINE("<P2PC|SERVER-ONLINE>"),
            LIST_SERVERS("<P2PC|LIST-SERVERS>");

            companion object {
                fun fromContent(content: String) = values().find { it.content == content }
            }
        }

    }

    private val socket = SimpleMulticastSocket(multicastAddress, multicastPort)
    private val subscriptions = mutableListOf<Pair<Payload, PayloadRecievedCallback>>()
    private var awationLoopActive = false

    fun sendMessage(payload: Payload) {
        socket.sendMessage(payload.content)
    }

    fun subscribeToPayload(payload: Payload, callback: PayloadRecievedCallback) {
        subscriptions += payload to callback
    }

    fun awaitNextMessage(timeoutMs: Int? = null): Payload? {
        val message = socket.readMessage(timeoutMs) ?: return null
        val payload = Payload.fromContent(message) ?: return null

        payload.invokeMatchingCallbacks()
        return payload
    }

    fun runAwationLoop() {
        awationLoopActive = true
        while (awationLoopActive) awaitNextMessage(50)
    }

    fun stopAwationLoop() {
        awationLoopActive = false
    }

    private fun Payload.invokeMatchingCallbacks() = subscriptions
        .filter { it.first == this }
        .map { it.second }
        .forEach { it.invoke() }

}

