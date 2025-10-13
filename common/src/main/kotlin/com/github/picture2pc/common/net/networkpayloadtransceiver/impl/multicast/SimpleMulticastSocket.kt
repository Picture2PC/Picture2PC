package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast

import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.payload.PayloadInfo
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.data.serialization.asByteArray
import com.github.picture2pc.common.net.data.serialization.fromByteArray
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetSocketAddress
import java.net.MulticastSocket
import java.net.NetworkInterface
import java.net.SocketTimeoutException


class SimpleMulticastSocket(
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val inetSocketAddress: InetSocketAddress
) {
    private lateinit var jvmMulticastSocket: MulticastSocket
    // get network interface for local dns

    suspend fun start(networkInterface: NetworkInterface) {
        withContext(ioDispatcher) {
            jvmMulticastSocket = MulticastSocket(inetSocketAddress.port)
            jvmMulticastSocket.soTimeout = MulticastConstants.POLLING_TIMEOUT
            jvmMulticastSocket.reuseAddress = true
            jvmMulticastSocket.networkInterface = networkInterface
            jvmMulticastSocket.loopbackMode = true // Disables loopback
            jvmMulticastSocket.joinGroup(inetSocketAddress, networkInterface)
        }
    }

    val isAvailable
        get() = this::jvmMulticastSocket.isInitialized && jvmMulticastSocket.isBound && !jvmMulticastSocket.isClosed

    suspend fun sendMessage(payload: Payload): Boolean {
        // Currently only supports Message-size < PACKET_SIZE
        if (!isAvailable) {
            return false
        }
        val byteArray = payload.asByteArray()
        val len = byteArray.size

        val datagramPacket = DatagramPacket(byteArray, len, inetSocketAddress)
        return kotlin.runCatching {
            withContext(ioDispatcher) {
                jvmMulticastSocket.send(
                    datagramPacket
                )
            }
        }.isSuccess

    }

    fun close() {
        jvmMulticastSocket.close()
    }

    suspend fun receivePayload(): Payload? {
        if (!isAvailable)
            return null
        val byteArray = ByteArray(MulticastConstants.PACKET_SIZE)
        val datagramPacket = DatagramPacket(byteArray, 0, MulticastConstants.PACKET_SIZE)
        try {
            withContext(ioDispatcher) {
                jvmMulticastSocket.receive(datagramPacket)
            }
        } catch (_: SocketTimeoutException) {
            return null
        } catch (_: IOException) {
            close()
            return null
        }
        val payload = Payload.fromByteArray(datagramPacket.data)
        if (payload.sourcePeer == Peer.getSelf())
            return null
        payload.receivedPayloadInfo =
            PayloadInfo(
                InetSocketAddress(
                    datagramPacket.address,
                    datagramPacket.port
                )
            )
        return payload
    }
}
