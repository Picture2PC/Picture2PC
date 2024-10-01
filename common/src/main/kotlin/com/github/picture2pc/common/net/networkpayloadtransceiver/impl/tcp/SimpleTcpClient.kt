package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp

import com.github.picture2pc.common.net.data.client.Client
import com.github.picture2pc.common.net.data.client.ClientState
import com.github.picture2pc.common.net.data.packet.Packet
import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.data.serialization.fromByteArray
import com.github.picture2pc.common.net.data.serialization.getByteArray
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpConstants.CONNECION_TIMEOUT
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpConstants.MAX_PACKET_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.ExperimentalSerializationApi
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalSerializationApi::class)
class SimpleTcpClient(
    private val backgroundScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val jvmSocket: Socket
) : Client()
{
    private val _receivedPayloads: MutableSharedFlow<Payload> = MutableSharedFlow(0, 1)
    override val receivedPayloads: SharedFlow<Payload> = _receivedPayloads.asSharedFlow()
    override var peer: Peer = Peer.any()

    private var isServer = false
    private var trusted: Boolean = false

    init {
        if (jvmSocket.isConnected) {
            isServer = true
            _clientStateFlow.value = ClientState.SUSPENDED
            backgroundScope.launch {
                startListen()
            }
        } else {
            trusted = true
        }
    }

    private suspend fun sendPing() {
        sendMessage(TcpPayload.Ping(Peer.any()))
    }

    private fun startListen() {
        backgroundScope.launch {
            while (backgroundScope.isActive) {
                val payload = receivePacket() ?: continue
                if (payload is TcpPayload.Ping) {
                    sendMessage(TcpPayload.Pong(peer))
                }
                _receivedPayloads.emit(payload)
            }
        }
    }

    suspend fun connect(inetSocketAddress: InetSocketAddress): Boolean {
        when (withTimeoutOrNull(CONNECION_TIMEOUT)
            {
                kotlin.runCatching {
                    withContext(ioDispatcher) {
                        jvmSocket.connect(inetSocketAddress)
                    }
                }.onFailure {
                    _clientStateFlow.value =
                        ClientState.DISCONNECTED.ERROR_WHILE_CONNECTING(it.message ?: "")
                    return@withTimeoutOrNull false
                }
                return@withTimeoutOrNull true
            }) {
            false -> return false
            null -> {
                _clientStateFlow.value = ClientState.DISCONNECTED.ERROR_WHILE_CONNECTING("Timeout")
                return false
            }
            true -> {
                startListen()
                sendPing()
                return true
            }
        }
    }

    suspend fun sendMessage(message: Payload): Boolean {
        val data = message.getByteArray()
        var size = 0
        val packetSize = max(data.size / 100, MAX_PACKET_SIZE)
        val prevState = _clientStateFlow.value
        kotlin.runCatching {
            withContext(ioDispatcher) {
                //send data in 100 steps
                while (size < data.size) {
                    jvmSocket.getOutputStream().write(data, size, min(data.size - size, packetSize))
                    _clientStateFlow.value = ClientState.SENDING_PAYLOAD(size / data.size.toFloat())
                    size += min(data.size - size, packetSize)
                }
            }
            _clientStateFlow.value = prevState
        }.onFailure {
            _clientStateFlow.value =
                ClientState.DISCONNECTED.ERROR_WHILE_SENDING("Error: ${it.message} while sending message $message")
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

    suspend fun disconnect() {
        close()
    }

    private suspend fun receivePacket(): Payload? {
        try {
            val sizeBuff = ByteArray(1024)
            var offset = 0
            withContext(ioDispatcher) {
                do {
                    jvmSocket.getInputStream().read(sizeBuff, offset, 1)
                } while (sizeBuff[offset++] != Byte.MIN_VALUE)
            }
            val p = Packet.fromByteArray(sizeBuff.copyOf(offset))
            val type = Class.forName(p.type).kotlin
            val size = p.len
            check(size > 0) { "Size is not positive" }
            val byteArray = ByteArray(size)
            var copied = 0
            withContext(ioDispatcher) {
                while (copied < size) {
                    _clientStateFlow.value = ClientState.RECEIVING_PAYLOAD(
                        type,
                        copied / size.toFloat()
                    )
                    copied += jvmSocket.getInputStream()
                        .read(byteArray, copied, size - copied)
                }
            }
            val pay = Payload.fromByteArray(byteArray)
            if (peer.isAny)
                peer = pay.sourcePeer
            _clientStateFlow.value = ClientState.CONNECTED
            return pay
        } catch (e: Exception) {
            _clientStateFlow.value = ClientState.DISCONNECTED.ERROR_WHILE_RECEIVING(e.message ?: "")
            return null
        }
    }
}