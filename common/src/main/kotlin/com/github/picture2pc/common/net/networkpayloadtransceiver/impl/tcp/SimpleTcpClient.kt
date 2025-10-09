package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp

import com.github.picture2pc.common.net.data.client.Client
import com.github.picture2pc.common.net.data.client.ClientState
import com.github.picture2pc.common.net.data.packet.Packet
import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.data.serialization.fromByteArray
import com.github.picture2pc.common.net.data.serialization.getByteArray
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpConstants.CONNECTION_TIMEOUT
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpConstants.MAX_PACKET_SIZE
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpConstants.PING_TIME
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpConstants.PING_TIMEOUT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.max
import kotlin.math.min

class SimpleTcpClient(
    private val backgroundScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val jvmSocket: Socket
) : Client() {
    private val _receivedPayloads: MutableSharedFlow<Payload> = MutableSharedFlow()
    override val receivedPayloads: SharedFlow<Payload> = _receivedPayloads
    override var peer: Peer = Peer.any()

    var isServer = false
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    private val singleIODispatcher = ioDispatcher.limitedParallelism(1)

    init {
        if (jvmSocket.isConnected) {
            isServer = true
            backgroundScope.launch {
                _clientStateFlow.emit(ClientState.SUSPENDED)
            }
        }
    }

    private suspend fun sendPing() {
        sendMessage(TcpPayload.Ping(Peer.any()).getByteArray())
    }

    fun startTimeout() {
        backgroundScope.launch {
            var timout = false
            while (backgroundScope.isActive) {
                kotlin.runCatching {
                    while (backgroundScope.isActive) {
                        withTimeout(if (isServer) PING_TIME else PING_TIMEOUT) {
                            kotlin.runCatching {
                                clientStateFlow.single()
                            }
                            timout = false
                        }
                    }
                }.onFailure {
                    if (isServer && !timout) {
                        sendPing()
                        timout = true
                    } else
                        disconnect(ClientState.DISCONNECTED.TIMEOUT)
                }
            }
        }
    }

    fun startListen(): Flow<Payload> = flow {
        while (backgroundScope.isActive) {
            val payload = receivePacket() ?: break
            if (payload is TcpPayload.Ping) {
                sendMessage(TcpPayload.Pong(peer).getByteArray())
            }
            emit(payload)
        }
    }

    suspend fun connect(inetSocketAddress: InetSocketAddress): Boolean {
        when (withTimeoutOrNull(CONNECTION_TIMEOUT)
        {
            kotlin.runCatching {
                withContext(ioDispatcher) {
                    jvmSocket.connect(inetSocketAddress)
                }
            }.onFailure {
                disconnect(
                    ClientState.DISCONNECTED.ERROR_WHILE_CONNECTING(it.message ?: "")
                )
                return@withTimeoutOrNull false
            }
            return@withTimeoutOrNull true
        }) {
            false -> return false
            null -> {
                disconnect(ClientState.DISCONNECTED.ERROR_WHILE_CONNECTING("Timeout"))
                return false
            }

            true -> {
                sendPing()
                return true
            }
        }
    }

    suspend fun sendMessage(data: ByteArray): Boolean {
        var size = 0
        val packetSize = max(data.size / 100, MAX_PACKET_SIZE)
        val prevState = _clientStateFlow.value
        kotlin.runCatching {
            withContext(singleIODispatcher) {
                //send data in 100 steps
                while (size < data.size) {
                    backgroundScope.ensureActive()
                    jvmSocket.getOutputStream().write(data, size, min(data.size - size, packetSize))
                    _clientStateFlow.emit(ClientState.SENDING_PAYLOAD(size / data.size.toFloat()))
                    size += min(data.size - size, packetSize)
                }
            }
            _clientStateFlow.emit(prevState)
        }.onFailure {
            disconnect(
                ClientState.DISCONNECTED.ERROR_WHILE_SENDING("Error: ${it.message} while sending message")
            )
            return false
        }
        return true
    }

    private suspend fun close() {
        kotlin.runCatching {
            withContext(ioDispatcher) {
                jvmSocket.close()
            }
        }
    }

    suspend fun disconnect(error: ClientState.DISCONNECTED) {
        _clientStateFlow.emit(error)
        close()
        backgroundScope.cancel()
    }

    /**
     * Receives a packet from the TCP socket using length-prefix protocol.
     * 
     * Protocol format:
     * [4-byte header length][header bytes][payload bytes]
     * 
     * This method:
     * 1. Reads the 4-byte length prefix to determine header size
     * 2. Reads the exact number of header bytes
     * 3. Decodes the Packet from header bytes to get payload size
     * 4. Reads the payload bytes based on the size from the Packet
     */
    private suspend fun receivePacket(): Payload? {
        try {
            // Read 4-byte length prefix
            val lengthPrefix = ByteArray(4)
            withContext(ioDispatcher) {
                var read = 0
                while (read < 4) {
                    val n = jvmSocket.getInputStream().read(lengthPrefix, read, 4 - read)
                    if (n < 0) throw Exception("End of stream while reading length prefix")
                    read += n
                    backgroundScope.ensureActive()
                }
            }
            
            // Decode header length from prefix
            val headerLength = ((lengthPrefix[0].toInt() and 0xFF) shl 24) or
                             ((lengthPrefix[1].toInt() and 0xFF) shl 16) or
                             ((lengthPrefix[2].toInt() and 0xFF) shl 8) or
                             (lengthPrefix[3].toInt() and 0xFF)
            
            // Read exact header bytes
            val headerBytes = ByteArray(headerLength)
            withContext(ioDispatcher) {
                var read = 0
                while (read < headerLength) {
                    val n = jvmSocket.getInputStream().read(headerBytes, read, headerLength - read)
                    if (n < 0) throw Exception("End of stream while reading header")
                    read += n
                    backgroundScope.ensureActive()
                }
            }
            
            val p = Packet.fromByteArray(headerBytes)
            val type = Class.forName(p.type).kotlin
            val size = p.len
            check(size > 0) { "Size is not positive" }
            val byteArray = ByteArray(size)
            var copied = 0
            withContext(ioDispatcher) {
                while (copied < size) {
                    backgroundScope.ensureActive()
                    _clientStateFlow.emit(
                        ClientState.RECEIVING_PAYLOAD(
                            type,
                            copied / size.toFloat()
                        )
                    )
                    copied += jvmSocket.getInputStream()
                        .read(byteArray, copied, size - copied)
                }
            }
            val pay = Payload.fromByteArray(byteArray)
            if (peer.isAny)
                peer = pay.sourcePeer
            _clientStateFlow.emit(ClientState.CONNECTED)
            return pay
        } catch (e: Exception) {
            disconnect(ClientState.DISCONNECTED.ERROR_WHILE_RECEIVING(e.message ?: ""))
            return null
        }
    }
}