package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp

import com.github.picture2pc.common.net.data.client.Client
import com.github.picture2pc.common.net.data.client.ClientState
import com.github.picture2pc.common.net.data.packet.Packet
import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.data.serialization.fromByteArray
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpConstants.CONNECION_TIMEOUT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.ExperimentalSerializationApi
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalSerializationApi::class)
class SimpleTcpClient(
    override val coroutineContext: CoroutineContext,
    override val peer: Peer,
    private val jvmSocket: Socket = Socket(),

    ) : CoroutineScope, Client() //, DefaultDataPayloadTransceiver()
{
    private var timeoutJob: Job? = null
    private val _receivedPayloads: MutableSharedFlow<Payload> = MutableSharedFlow()
    override val receivedPayloads: SharedFlow<Payload> = _receivedPayloads.asSharedFlow()
    val socketAddress
        get() = jvmSocket.localSocketAddress as InetSocketAddress

    val isConnected
        get() = jvmSocket.isConnected && !jvmSocket.isClosed

    fun startReceiving() {
        _clientStateFlow.value = ClientState.CONNECTED
        timeoutJob = getTimeoutJob()
        launch {
            while (isActive) {
                val packet = receivePacket() ?: break
                if (packet is TcpPayload.Ping) {
                    handlePing()
                    continue
                }
                _receivedPayloads.tryEmit(packet)
            }
        }
    }

    private fun getTimeoutJob(): Job {
        return launch {
            delay(TcpConstants.PINGTIME)
            sendMessage(TcpPayload.Ping(peer))
            delay(TcpConstants.PINGTIMEOUT - TcpConstants.PINGTIME)
            _clientStateFlow.value = ClientState.DISCONNECTED.ERROR_WHILE_RECEIVING("Ping timeout")
        }
    }

    suspend fun connect(inetSocketAddress: InetSocketAddress): Boolean {
        if (coroutineScope {
            return@coroutineScope withTimeoutOrNull(CONNECION_TIMEOUT)
            {
                kotlin.runCatching {
                    jvmSocket.connect(inetSocketAddress)
                }.onFailure {
                    return@withTimeoutOrNull null
                }
                return@withTimeoutOrNull true
            }
            } == null) {
            _clientStateFlow.value =
                ClientState.DISCONNECTED.ERROR_WHILE_CONNECTING("Connection timeout")
            return false
        }
        return true
    }

    private suspend fun handlePing() {
        sendMessage(TcpPayload.Pong(peer))
    }

    suspend fun sendMessage(message: Payload): Boolean {
        val data = getByteArray(message)

        return coroutineScope {
            try {
                jvmSocket.getOutputStream().write(data)
            } catch (e: Exception) {
                _clientStateFlow.value =
                    ClientState.DISCONNECTED.ERROR_WHILE_SENDING(e.message ?: "")
                return@coroutineScope false
            }
            return@coroutineScope true
        }
    }

    private suspend fun close() {
        coroutineScope {
            try {
                jvmSocket.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun disconnect() {
        close()
    }

    suspend fun receivePacket(): Payload? {
        try {
            val sizeBuff = ByteArray(1024)
            var offset = 0
            coroutineScope {
                do {
                    jvmSocket.getInputStream().read(sizeBuff, offset, 1)
                } while (sizeBuff[offset++] != Byte.MIN_VALUE)
            }
            val p = Packet.fromByteArray(sizeBuff.copyOf(offset))
            _clientStateFlow.value = ClientState.RECEIVING_PAYLOAD(0.1f)
            timeoutJob?.cancelAndJoin()
            val size = p.len
            check(size > 0) { "Size is not positive" }
            val byteArray = ByteArray(size)
            var copied = 0
            coroutineScope {
                while (copied < size) {
                    copied += jvmSocket.getInputStream()
                        .read(byteArray, copied, size - copied)
                }
            }
            timeoutJob = getTimeoutJob()
            _clientStateFlow.value = ClientState.CONNECTED
            return Payload.fromByteArray(byteArray)
        } catch (e: Exception) {
            _clientStateFlow.value = ClientState.DISCONNECTED.ERROR_WHILE_RECEIVING(e.message ?: "")
            return null
        }
    }
}