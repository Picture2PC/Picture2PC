package com.github.picture2pc.common.net.impl.multicast

import com.github.picture2pc.common.net.impl.common.NetworkHelper
import com.github.picture2pc.common.net.payloads.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.DatagramPacket
import java.net.InetSocketAddress
import java.net.SocketTimeoutException
import kotlin.coroutines.CoroutineContext


internal class SimpleMulticastSocket(
    private val inetSocketAddress: InetSocketAddress,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    private val jvmMulticastSocket = java.net.MulticastSocket(inetSocketAddress.port)

    init {
        // get network interface for local dns
        jvmMulticastSocket.soTimeout = MulticastConstants.POLLING_TIMEOUT
        jvmMulticastSocket.networkInterface = NetworkHelper.getDefaultNetworkInterface()
        jvmMulticastSocket.joinGroup(inetSocketAddress, null)
    }

    val isAvailable
        get() = jvmMulticastSocket.isBound && !jvmMulticastSocket.isClosed

    suspend fun sendMessage(message: InputStream): Boolean {
        // Currently only supports Message-size < PACKET_SIZE
        if (!isAvailable) {
            return false
        }
        return coroutineScope {
            if (message.available() == 0) {
                return@coroutineScope false
            }
            val len = message.available()
            require(len < MulticastConstants.PACKET_SIZE) { "Must be less than PACKET_SIZE" }
            val datagramPacket = DatagramPacket(message.readBytes(), len, inetSocketAddress)
            return@coroutineScope kotlin.runCatching { jvmMulticastSocket.send(datagramPacket) }.isSuccess
        }
    }

    private fun close() {
        jvmMulticastSocket.close()
    }

    suspend fun receivePacket(): Payload? {
        if (!isAvailable)
            return null
        val byteArray = ByteArray(MulticastConstants.PACKET_SIZE)
        val datagramPacket = DatagramPacket(byteArray, byteArray.size)
        try {
            coroutineScope {
                jvmMulticastSocket.receive(datagramPacket)
            }
        } catch (e: SocketTimeoutException) {
            coroutineScope {
                kotlin.runCatching {
                    val new = NetworkHelper.getDefaultNetworkInterface()
                    if (new != jvmMulticastSocket.networkInterface) {
                        jvmMulticastSocket.networkInterface = new
                        jvmMulticastSocket.joinGroup(inetSocketAddress, null)
                    }
                }
            }
            return null
        } catch (e: IOException) {
            close()
            return null
        }
        return Payload.fromInputStream(
            ByteArrayInputStream(datagramPacket.data),
            InetSocketAddress(datagramPacket.address, datagramPacket.port)
        )
    }
}
