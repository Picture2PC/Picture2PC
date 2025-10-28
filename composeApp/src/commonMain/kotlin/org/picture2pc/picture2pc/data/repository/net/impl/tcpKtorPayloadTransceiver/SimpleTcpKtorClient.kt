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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.mp.KoinPlatformTools
import org.picture2pc.picture2pc.data.repository.net.packet.Packet
import org.picture2pc.picture2pc.data.repository.net.payload.Payload
import org.picture2pc.picture2pc.data.repository.net.peer.Peer
import org.picture2pc.picture2pc.data.repository.net.serialization.asByteArray
import org.picture2pc.picture2pc.data.repository.net.serialization.fromByteArray
import org.picture2pc.picture2pc.domain.repository.net.client.ClientSecurityState
import org.picture2pc.picture2pc.domain.repository.net.client.ClientState

class SimpleTcpKtorClient(private val socket: Socket, peer: Peer = Peer.any()) {
    var peer: Peer = peer
        private set

    private val _clientStateFlow = MutableStateFlow<Pair<ClientSecurityState, ClientState>>(
        Pair(
            if (peer.isAny) ClientSecurityState.PeerUnknown else ClientSecurityState.PeerKnown.UnVerified(
                peer
            ),
            ClientState.CONNECTED
        )
    )
    val clientStateFlow = _clientStateFlow.asStateFlow()
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
                writeChannel.writeInt(packetBytes.size)
                writeChannel.writeByteArray(packetBytes)
                writeChannel.writeByteArray(payloadBytes)
                writeChannel.flush()
                // Sending done
            }
        }.onFailure {
            socket.close()
            return false
        }
        return true
    }

    fun receivePayloads(): Flow<Payload> = callbackFlow {
        while (isActive) {
            runCatching {
                val readChannel = socket.openReadChannel()
                val headerLength = readChannel.readInt()
                val header = Packet.fromByteArray(readChannel.readByteArray(headerLength))
                val byteArray = ByteArray(header.len)
                with(readChannel.counted()) {
                    while (this.totalBytesRead.toInt() != byteArray.size) {
                        this.readAvailable(byteArray, totalBytesRead.toInt())
                    }
                }
                send(Payload.fromByteArray(byteArray))
            }.onFailure {
                socket.close()
            }
        }
    }
}