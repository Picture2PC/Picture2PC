package org.picture2pc.picture2pc.data.repository.net.impl.multicastPayloadTransceiver

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import org.picture2pc.picture2pc.data.repository.net.payload.DiscoverPayload
import org.picture2pc.picture2pc.data.repository.net.payload.Payload
import org.picture2pc.picture2pc.domain.repository.net.ClientDiscovery
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface

actual class MulticastPayloadTransceiver actual constructor(
    val scope: CoroutineScope,
    val ioDispatcher: CoroutineDispatcher
) :
    ClientDiscovery {
    companion object {
        init {
            System.setProperty("java.net.preferIPv4Stack", "true")
        }

        fun getDefaultNetworkInterfaces(): Sequence<NetworkInterface> {
            val possible = NetworkInterface.getNetworkInterfaces().asSequence().filter(::isPossible)
            return possible
        }

        private fun isPossible(networkInterface: NetworkInterface): Boolean {
            return (networkInterface.isUp && !networkInterface.isVirtual && !networkInterface.isLoopback
                    && networkInterface.supportsMulticast() && !networkInterface.name.startsWith(
                "vEthernet",
                true
            ) && !networkInterface.isPointToPoint && networkInterface.inetAddresses.asSequence()
                .any { !it.isLoopbackAddress && it::class == Inet4Address::class })
        }
    }

    private val multicastSockets: MutableMap<NetworkInterface, SimpleMulticastSocket> =
        mutableMapOf()

    actual override val available: Boolean
        get() = multicastSockets.values.any { it.isAvailable }

    actual override fun discover(serviceOnlineProvider: () -> DiscoverPayload.ServiceOnline): Flow<DiscoverPayload.ServiceOnline> =
        channelFlow {
            while (true) {
                updateMulticastSockets(this)
                emitServerOnline(serviceOnlineProvider())
                delay(MulticastConstants.UPDATE_INTERFACE_DELAY)
            }
        }

    private suspend fun updateMulticastSockets(collector: ProducerScope<DiscoverPayload.ServiceOnline>) {
        while (runCatching {
                val interfaces = getDefaultNetworkInterfaces().toSet()
                val newInterfaces = interfaces.minus(multicastSockets.keys)
                val removeInterfaces = multicastSockets.keys.minus(interfaces)
                newInterfaces.forEach {
                    startSingleSocket(it, collector)
                }
                removeInterfaces.forEach {
                    stopSingleSocket(it)
                }

            }.isFailure) {
            delay(MulticastConstants.RETRY_DELAY)
        }
    }

    private suspend fun startSingleSocket(
        networkInterface: NetworkInterface,
        collector: ProducerScope<DiscoverPayload.ServiceOnline>
    ) {
        val multicastSocket = SimpleMulticastSocket(
            ioDispatcher,
            InetSocketAddress(MulticastConstants.ADDRESS, MulticastConstants.PORT)
        )
        multicastSocket.start(networkInterface)
        multicastSockets[networkInterface] = multicastSocket
        multicastSocket.receivePayload().onEach {
            if (it is DiscoverPayload) handlePayload(it, collector)
        }.onCompletion { stopSingleSocket(networkInterface) }.launchIn(collector)
    }

    private suspend fun emitServerOnline(serviceOnline: DiscoverPayload.ServiceOnline) {
        sendPayload(serviceOnline)
    }

    private fun handlePayload(
        payload: DiscoverPayload,
        collector: ProducerScope<DiscoverPayload.ServiceOnline>
    ) {
        when (payload) {
            is DiscoverPayload.ServiceOnline ->
                payload.serviceAddress?.let {
                    collector.trySend(
                        payload
                    )
                }

            is DiscoverPayload.ServicesList ->
                null
            //emitServerOnline()
        }
    }
    private fun stopSingleSocket(networkInterface: NetworkInterface) {
        multicastSockets.remove(networkInterface)?.close()
    }

    private suspend fun sendPayload(payload: Payload): Boolean {
        val jobs = multicastSockets.values.map { scope.async { it.sendMessage(payload) } }

        return jobs.awaitAll().any()
    }
}