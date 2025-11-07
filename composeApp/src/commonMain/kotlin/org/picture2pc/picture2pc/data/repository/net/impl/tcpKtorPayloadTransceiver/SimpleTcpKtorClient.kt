package org.picture2pc.picture2pc.data.repository.net.impl.tcpKtorPayloadTransceiver

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
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
    private val writeChannel = socket.openWriteChannel(autoFlush = true)
    private val writeMutex = Mutex()

    val connectedAddress: InetSocketAddress?
        get() = if (!isClosed) socket.remoteAddress as InetSocketAddress else null

    val isClosed: Boolean
        get() = socket.isClosed

    suspend fun sendPayload(payload: Payload): Boolean {
        val encryptedState = client.securityState.value as? ClientSecurityState.PeerKnown.Encrypted
        val payloadBytes = encryptedState?.sharedKey?.encrypt(payload.asByteArray()) ?: payload.asByteArray()
        val packet = Packet(
            KoinPlatformTools.getClassName(payload::class),
            payloadBytes.size,
            payload.sourcePeer,
            encryptedState != null,
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
        while (!readChannel.isClosedForRead) {
            runCatching {
                val headerLength = readChannel.readInt()
                val headerByteArray = ByteArray(headerLength)
                readChannel.readFully(headerByteArray)
                val header = Packet.fromByteArray(headerByteArray)
                val byteArray = ByteArray(header.len)

                var totalRead = 0
                while (totalRead < byteArray.size) {
                    client._state.tryEmit(
                        ClientState.RECEIVING_PAYLOAD(
                            header.type,
                            totalRead.toFloat() / byteArray.size
                        )
                    )
                    totalRead += readChannel.readAvailable(
                        byteArray,
                        totalRead,
                        byteArray.size - totalRead
                    )
                }

                if (client.securityState.value is ClientSecurityState.PeerUnknown && !header.sourcePeer.isAny) {
                    client._securityState.emit(ClientSecurityState.PeerKnown.UnVerified(header.sourcePeer))
                }
                client._state.emit(ClientState.CONNECTED)
                runCatching {
                    if (header.encrypted) {
                        (client.securityState.value as? ClientSecurityState.PeerKnown.Encrypted)?.sharedKey?.decrypt(
                            byteArray
                        )?.let {
                            trySend(Payload.fromByteArray(it))
                        }
                    } else {
                        trySend(Payload.fromByteArray(byteArray))
                    }
                }
            }.onFailure {
                close(ClientState.DISCONNECTED.ERROR_WHILE_RECEIVING(it.toString()))
                return@channelFlow
            }
        }
    }
}