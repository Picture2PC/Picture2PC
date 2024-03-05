package com.github.picture2pc.common.net.multicast

import com.github.picture2pc.common.net.common.NetworkPacket
import com.github.picture2pc.common.net.common.ReceivedMulticastPacket
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.SocketTimeoutException


internal class SimpleMulticastSocket(
    address: String,
    port: Int,
) {
    private val socketAddress = InetSocketAddress(address, port)
    private val jvmMulticastSocket = java.net.MulticastSocket(port)

    init {
        val networkInterface = NetworkInterface.getNetworkInterfaces().asSequence().filter { i -> i.isUp && !i.isVirtual && !i.isLoopback && !i.name.startsWith("vEthernet", true) && i.supportsMulticast() }.maxByOrNull { r -> r.interfaceAddresses.size }
        jvmMulticastSocket.networkInterface = networkInterface
        jvmMulticastSocket.joinGroup(socketAddress, null)

    }

    fun sendMessage(message: InputStream) {
        val packet = NetworkPacket(message, socketAddress)
        while (packet.available)
            jvmMulticastSocket.send(packet.getDatagramPacket())
    }

    fun recievePacket(timeoutMs: Int? = null): ReceivedMulticastPacket? {
        jvmMulticastSocket.soTimeout = (timeoutMs ?: 0).coerceAtLeast(0)

        val packet = NetworkPacket();
        while (packet.available){
            try {
                jvmMulticastSocket.receive(packet.getDatagramPacket())
            }
            catch (e: SocketTimeoutException) {
                return null
            }
        }

        return ReceivedMulticastPacket(packet.getInputStream(), packet.getAddress())
    }

    private fun String.removeNullBytes() = replace("\u0000", "")

}
