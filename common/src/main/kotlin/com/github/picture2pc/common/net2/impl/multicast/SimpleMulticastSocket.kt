package com.github.picture2pc.common.net2.impl.multicast

import com.github.picture2pc.common.net2.common.NetworkPacket
import com.github.picture2pc.common.net2.common.ReceivedMulticastPacket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.SocketTimeoutException


internal class SimpleMulticastSocket(
    val inetSocketAddress: InetSocketAddress
) {
    private val jvmMulticastSocket = java.net.MulticastSocket(inetSocketAddress.port)

    init {
        jvmMulticastSocket.networkInterface =
            NetworkInterface.getNetworkInterfaces().asSequence().filter { i ->
                i.isUp && !i.isVirtual && !i.isLoopback && !i.name.startsWith(
                    "vEthernet",
                    true
                ) && i.supportsMulticast()
            }.maxByOrNull { r -> r.interfaceAddresses.size }!!
        jvmMulticastSocket.loopbackMode = true
        jvmMulticastSocket.soTimeout = 0
        jvmMulticastSocket.joinGroup(inetSocketAddress, null)
    }

    suspend fun sendMessage(message: InputStream) {
        val packet = NetworkPacket(message, inetSocketAddress)
        while (packet.available) {
            withContext(Dispatchers.IO) {
                jvmMulticastSocket.send(packet.getDatagramPacket())
            }
            //delay(10) //TODO: check if this is necessary
        }

    }

    fun receivePacket(): ReceivedMulticastPacket? {
        val packet = NetworkPacket()
        while (packet.available) {
            try {
                jvmMulticastSocket.receive(packet.getDatagramPacket())
            } catch (e: SocketTimeoutException) {
                return null
            }
        }
        //TODO Handle if multiple packages from different host are received at the same time
        return ReceivedMulticastPacket(packet.getInputStream(), packet.getAddress())
    }
}
