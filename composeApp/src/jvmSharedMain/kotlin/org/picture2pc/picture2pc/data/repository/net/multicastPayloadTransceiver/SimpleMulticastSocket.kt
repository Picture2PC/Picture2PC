package org.picture2pc.picture2pc.data.repository.net.multicastPayloadTransceiver

import co.touchlab.kermit.Logger
import io.ktor.util.network.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import org.picture2pc.picture2pc.data.repository.net.common.peer.Peer
import org.picture2pc.picture2pc.domain.repository.net.payload.Payload
import org.picture2pc.picture2pc.domain.repository.net.payload.PayloadInfo
import org.picture2pc.picture2pc.domain.serialization.asByteArray
import org.picture2pc.picture2pc.domain.serialization.fromByteArray
import java.io.IOException
import java.net.*


class SimpleMulticastSocket(
    private val ioDispatcher: CoroutineDispatcher,
    private val inetSocketAddress: InetSocketAddress,
    private val logger: Logger
) {
    private lateinit var jvmMulticastSocket: MulticastSocket
    // get network interface for local dns

    suspend fun start(networkInterface: NetworkInterface) {
        jvmMulticastSocket = MulticastSocket(inetSocketAddress.port)
        withContext(ioDispatcher) {
            jvmMulticastSocket.apply {
                soTimeout = MulticastConstants.POLLING_TIMEOUT
                reuseAddress = true
                this.networkInterface = networkInterface
                loopbackMode = true // Disables loopback
                joinGroup(inetSocketAddress, networkInterface)
            }
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
        return runCatching {
            withContext(ioDispatcher) {
                jvmMulticastSocket.send(
                    datagramPacket
                )
            }
        }.onFailure { logger.e("Failed to send ${payload::class.simpleName} to ${payload.targetPeer.uuid}") }.isSuccess

    }

    fun close() {
        if (isAvailable)
            jvmMulticastSocket.close()
    }

    fun receivePayload(): Flow<Payload> = callbackFlow {
        loop@ while (true) {
            ensureActive()
            if (!isAvailable)
                close()
            val byteArray = ByteArray(MulticastConstants.PACKET_SIZE)
            val datagramPacket = DatagramPacket(byteArray, 0, MulticastConstants.PACKET_SIZE)
            try {
                withContext(ioDispatcher) {
                    jvmMulticastSocket.receive(datagramPacket)
                }
            } catch (_: SocketTimeoutException) {
                continue
            } catch (_: IOException) {
                close()
            }
            runCatching {
                val payload = Payload.fromByteArray(datagramPacket.data)
                if (payload.sourcePeer == Peer.getSelf())
                    continue@loop
                payload.receivedPayloadInfo =
                    PayloadInfo(
                        NetworkAddress(datagramPacket.address.hostName, datagramPacket.port)
                    )
                this.trySend(payload)
            }.onFailure {
                logger.w("Failed to receive payload", it)
            }

        }
    }
}
