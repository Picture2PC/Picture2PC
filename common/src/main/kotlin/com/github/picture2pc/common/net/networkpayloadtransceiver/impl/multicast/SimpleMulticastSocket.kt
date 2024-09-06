package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast

import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.payload.PayloadInfo
import com.github.picture2pc.common.net.data.serialization.fromByteArray
import com.github.picture2pc.common.net.extentions.getDefaultNetworkInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetSocketAddress
import java.net.MulticastSocket
import java.net.SocketTimeoutException
import kotlin.coroutines.CoroutineContext


internal class SimpleMulticastSocket(
    private val inetSocketAddress: InetSocketAddress,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    private val jvmMulticastSocket = MulticastSocket(inetSocketAddress.port)

    init {
        // get network interface for local dns
        jvmMulticastSocket.soTimeout = MulticastConstants.POLLING_TIMEOUT
        jvmMulticastSocket.networkInterface = getDefaultNetworkInterface()
        jvmMulticastSocket.joinGroup(inetSocketAddress, null)
    }

    val isAvailable
        get() = jvmMulticastSocket.isBound && !jvmMulticastSocket.isClosed

    suspend fun sendMessage(message: ByteArray): Boolean {
        // Currently only supports Message-size < PACKET_SIZE
        if (!isAvailable) {
            return false
        }
        return coroutineScope {
            val len = message.size
            require(len < MulticastConstants.PACKET_SIZE) { "Must be less than PACKET_SIZE" }
            val datagramPacket = DatagramPacket(message, len, inetSocketAddress)
            return@coroutineScope kotlin.runCatching { jvmMulticastSocket.send(datagramPacket) }.isSuccess
        }
    }

    private fun close() {
        jvmMulticastSocket.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun receivePacket(): Payload? {
        if (!isAvailable)
            return null
        val byteArray = ByteArray(MulticastConstants.PACKET_SIZE)
        val datagramPacket = DatagramPacket(byteArray, byteArray.size, inetSocketAddress)
        try {
            coroutineScope {
                jvmMulticastSocket.receive(datagramPacket)
            }
        } catch (e: SocketTimeoutException) {
            kotlin.runCatching {
                coroutineScope {
                    val new = getDefaultNetworkInterface()

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
        val payload = Payload.fromByteArray(datagramPacket.data)
        payload.receivedPayloadInfo =
            PayloadInfo(InetSocketAddress(datagramPacket.address, datagramPacket.port))
        return payload
    }
}
