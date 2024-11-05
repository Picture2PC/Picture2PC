package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast

import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.payload.PayloadInfo
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.data.serialization.asByteArray
import com.github.picture2pc.common.net.data.serialization.fromByteArray
import com.github.picture2pc.common.net.extentions.getDefaultNetworkInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetSocketAddress
import java.net.MulticastSocket
import java.net.SocketTimeoutException


class SimpleMulticastSocket(
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val inetSocketAddress: InetSocketAddress
) {
    private lateinit var jvmMulticastSocket: MulticastSocket
    private var lock: Mutex = Mutex()
    // get network interface for local dns

    suspend fun start() {
        withContext(ioDispatcher) {
            jvmMulticastSocket = MulticastSocket(inetSocketAddress.port)
            jvmMulticastSocket.soTimeout = MulticastConstants.POLLING_TIMEOUT
            jvmMulticastSocket.networkInterface = getDefaultNetworkInterface()
            jvmMulticastSocket.joinGroup(inetSocketAddress, null)
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

    private fun close() {
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
        } catch (e: SocketTimeoutException) {
            scope.launch(ioDispatcher) {
                if (lock.isLocked)
                    return@launch
                lock.withLock {
                kotlin.runCatching {
                    val new = getDefaultNetworkInterface()
                    if (new != jvmMulticastSocket.networkInterface) {
                        jvmMulticastSocket.networkInterface = new
                        jvmMulticastSocket.joinGroup(inetSocketAddress, null)
                    }
                }
                }
            }
            return null
        } catch (e: IOException) {
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
