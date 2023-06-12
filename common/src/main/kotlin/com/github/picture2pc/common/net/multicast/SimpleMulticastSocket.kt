package com.github.picture2pc.common.net.multicast

import java.net.DatagramPacket
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.SocketTimeoutException


internal class SimpleMulticastSocket(
    address: String,
    port: Int,
    private val recieveBufferSize: Int = 1024
) {
    private val socketAddress = InetSocketAddress(address, port)
    private val jvmMulticastSocket = java.net.MulticastSocket(port)

    init {
        val networkInterface = NetworkInterface.getByInetAddress(socketAddress.address)
        jvmMulticastSocket.joinGroup(socketAddress, networkInterface)
    }

    fun sendMessage(message: String) {
        val buffer = message.encodeToByteArray()
        val packet = DatagramPacket(buffer, buffer.size, socketAddress)
        jvmMulticastSocket.send(packet)
    }

    fun recievePacket(timeoutMs: Int? = null): ReceivedMulticastPacket? {
        jvmMulticastSocket.soTimeout = (timeoutMs ?: 0).coerceAtLeast(0)

        val buffer = ByteArray(recieveBufferSize)
        val packet = DatagramPacket(buffer, buffer.size)

        try {
            jvmMulticastSocket.receive(packet)
        }
        catch (e: SocketTimeoutException) {
            return null
        }

        val content = buffer.decodeToString().removeNullBytes()
        val address = packet.address.hostAddress

        return ReceivedMulticastPacket(content, address)
    }

    private fun String.removeNullBytes() = replace("\u0000", "")

}
