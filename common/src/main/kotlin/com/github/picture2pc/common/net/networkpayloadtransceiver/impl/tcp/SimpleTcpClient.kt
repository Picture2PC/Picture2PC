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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
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

@OptIn(ExperimentalSerializationApi::class)
class SimpleTcpClient(
    private val backgroundScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    override val peer: Peer,
    private val jvmSocket: Socket = Socket(),
) : Client() //, DefaultDataPayloadTransceiver()
{
    private var timeoutJob: Job? = null
    private val _receivedPayloads: MutableSharedFlow<Payload> = MutableSharedFlow(0, 1)
    override val receivedPayloads: SharedFlow<Payload> = _receivedPayloads.asSharedFlow()

    val isConnected
        get() = jvmSocket.isConnected && !jvmSocket.isClosed

    fun startReceiving() {
        _clientStateFlow.value = ClientState.CONNECTED
        timeoutJob = getTimeoutJob()
        backgroundScope.launch {
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
        return backgroundScope.launch {
            delay(TcpConstants.PINGTIME)
            sendMessage(TcpPayload.Ping(peer))
            delay(TcpConstants.PINGTIMEOUT - TcpConstants.PINGTIME)
            _clientStateFlow.value = ClientState.DISCONNECTED.ERROR_WHILE_RECEIVING("Ping timeout")
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

            true -> return true
        }
    }

    private suspend fun handlePing() {
        sendMessage(TcpPayload.Pong(peer))
    }

    suspend fun sendMessage(message: Payload): Boolean {
        val data = message.getByteArray()
        kotlin.runCatching {
            withContext(ioDispatcher) {
                //send data in 100 steps
                val step = data.size / 100
                for (i in 0 until 100) {
                    jvmSocket.getOutputStream().write(data, i * step, step)
                    _clientStateFlow.value = ClientState.SENDING_PAYLOAD(i / 100f)
                }
            }
        }.onFailure {
            _clientStateFlow.value =
                ClientState.DISCONNECTED.ERROR_WHILE_SENDING(it.message ?: "")
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

    suspend fun receivePacket(): Payload? {
        try {
            val sizeBuff = ByteArray(1024)
            var offset = 0
            withContext(ioDispatcher) {
                do {
                    jvmSocket.getInputStream().read(sizeBuff, offset, 1)
                } while (sizeBuff[offset++] != Byte.MIN_VALUE)
            }
            val p = Packet.fromByteArray(sizeBuff.copyOf(offset))
            timeoutJob?.cancelAndJoin()
            val size = p.len
            check(size > 0) { "Size is not positive" }
            val byteArray = ByteArray(size)
            var copied = 0
            withContext(ioDispatcher) {
                while (copied < size) {
                    _clientStateFlow.value = ClientState.RECEIVING_PAYLOAD(
                        Class.forName(p.type).kotlin,
                        copied / size.toFloat()
                    )
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