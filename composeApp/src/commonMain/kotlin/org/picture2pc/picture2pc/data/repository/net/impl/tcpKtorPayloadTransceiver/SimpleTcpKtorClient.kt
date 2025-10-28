package org.picture2pc.picture2pc.data.repository.net.impl.tcpKtorPayloadTransceiver

import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.isClosed
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.counted
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.readByteArray
import io.ktor.utils.io.readInt
import io.ktor.utils.io.writeByteArray
import io.ktor.utils.io.writeInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.mp.KoinPlatformTools
import org.picture2pc.picture2pc.data.repository.net.impl.MulticastTcpClient
import org.picture2pc.picture2pc.data.repository.net.packet.Packet
import org.picture2pc.picture2pc.data.repository.net.payload.Payload
import org.picture2pc.picture2pc.data.repository.net.serialization.asByteArray
import org.picture2pc.picture2pc.data.repository.net.serialization.fromByteArray
import org.picture2pc.picture2pc.domain.repository.net.client.ClientSecurityState
import org.picture2pc.picture2pc.domain.repository.net.client.ClientState

class SimpleTcpKtorClient(private val socket: Socket, val client: MulticastTcpClient) {
    private val writeChannel = socket.openWriteChannel(autoFlush = false)
    private val writeMutex = Mutex()

    val connectedAddress: InetSocketAddress?
        get() = if (!isClosed) socket.remoteAddress as InetSocketAddress else null

    val isClosed: Boolean
        get() = socket.isClosed

    suspend fun sendPayload(payload: Payload): Boolean {
        val payloadBytes = payload.asByteArray()
        val packet = Packet(
            KoinPlatformTools.getClassName(payload::class),
            payloadBytes.size,
            payload.sourcePeer
        )
        val packetBytes = packet.asByteArray()
        kotlin.runCatching {
            writeMutex.withLock {
                // Sending Start
                client._state.emit(ClientState.SENDING_PAYLOAD(0f))
                writeChannel.writeInt(packetBytes.size)
                writeChannel.writeByteArray(packetBytes)
                writeChannel.writeByteArray(payloadBytes)
                writeChannel.flush()
                // Sending done
                client._state.emit(ClientState.CONNECTED)
                delay(10)
            }
        }.onFailure {
            close(ClientState.DISCONNECTED.ERROR_WHILE_SENDING(it.toString()))
            return false
        }
        return true
    }

    suspend fun close(clientState: ClientState.DISCONNECTED = ClientState.DISCONNECTED.OTHER_ERROR("Socket error")) {
        socket.close()
        client._state.emit(clientState)
    }

    fun receivePayloads(): Flow<Payload> = channelFlow {
        val readChannel = socket.openReadChannel()
        while (true) {
            runCatching {
                val headerLength = readChannel.readInt()
                val header = Packet.fromByteArray(readChannel.readByteArray(headerLength))
                val byteArray = ByteArray(header.len)
                with(readChannel.counted()) {
                    while (this.totalBytesRead.toInt() != byteArray.size) {
                        client._state.tryEmit(
                            ClientState.RECEIVING_PAYLOAD(
                                header.type,
                                totalBytesRead.toFloat() / byteArray.size
                            )
                        )
                        this.readAvailable(byteArray, totalBytesRead.toInt())
                    }
                }
                if (client.securityState.value is ClientSecurityState.PeerUnknown) {
                    client._securityState.emit(ClientSecurityState.PeerKnown.UnVerified(header.sourcePeer))
                }
                client._state.emit(ClientState.CONNECTED)
                trySend(Payload.fromByteArray(byteArray))
            }.onFailure {
                close(ClientState.DISCONNECTED.ERROR_WHILE_RECEIVING(it.toString()))
                return@channelFlow
            }
        }
    }
}