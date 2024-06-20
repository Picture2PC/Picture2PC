package com.github.picture2pc.common.net2.impl.multicast

import com.github.picture2pc.common.net2.impl.common.NetworkHelper
import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.DatagramPacket
import java.net.InetSocketAddress
import java.net.SocketTimeoutException
import kotlin.coroutines.CoroutineContext


internal class SimpleMulticastSocket(
    val inetSocketAddress: InetSocketAddress, override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    private val jvmMulticastSocket = java.net.MulticastSocket(inetSocketAddress.port)

    init {
        // get networkinterface for local dns
        jvmMulticastSocket.loopbackMode = true
        jvmMulticastSocket.soTimeout = 0
        jvmMulticastSocket.joinGroup(inetSocketAddress, null)
    }

    suspend fun updateNetworkInterface() {
        return
        jvmMulticastSocket.networkInterface =
            coroutineScope { NetworkHelper.getDefaultNetworkInterface() } ?: return
    }

    suspend fun sendMessage(message: InputStream): Boolean {
        // Currently only supports Messagesize < 1024*16
        if (!jvmMulticastSocket.isBound) {
            return false
        }
        return coroutineScope {
            if (message.available() == 0) {
                return@coroutineScope false
            }
            val len = message.available()
            assert(len < 1024 * 16)
            val datagramPacket = DatagramPacket(message.readBytes(), len, inetSocketAddress)
            jvmMulticastSocket.send(datagramPacket)
            return@coroutineScope true
        }
    }

    suspend fun receivePacket(): Payload? {
        val byteArray = ByteArray(1024 * 16)
        val datagramPacket = DatagramPacket(byteArray, byteArray.size)
        try {
            coroutineScope {
                jvmMulticastSocket.receive(datagramPacket)
            }
        } catch (e: SocketTimeoutException) {
            return null
        }

        //TODO Handle if multiple packages from different host are received at the same time
        return Payload.fromInputStream(
            ByteArrayInputStream(datagramPacket.data),
            InetSocketAddress(datagramPacket.address, datagramPacket.port)
        )
    }
}
