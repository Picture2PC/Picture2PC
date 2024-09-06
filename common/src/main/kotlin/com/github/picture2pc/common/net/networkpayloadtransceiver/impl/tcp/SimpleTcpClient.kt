package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp

import com.github.picture2pc.common.net.data.client.Client
import com.github.picture2pc.common.net.data.client.ClientState
import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpConstants.CONNECION_TIMEOUT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import kotlinx.serialization.ExperimentalSerializationApi
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
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
//        timeoutJob = getTimeoutJob()
        launch {
            while (isActive) {
                val packet = receivePacket()
                if (packet == null) {
                    disconnect()
                    break
                }
                if (packet is TcpPayload.Ping) {
//                    handlePing()
                    continue
                }
                _receivedPayloads.tryEmit(packet)
            }
        }

//        _clientStateFlow.onEach {
//            if (it is ClientState.RECEIVING_PAYLOAD) {
//                timeoutJob!!.cancelAndJoin()
//                timeoutJob = getTimeoutJob()
//            }
//        }.launchIn(this)
    }

//    private fun getTimeoutJob(): Job {
//        return launch {
//            delay(TcpConstants.PINGTIME)
//            if (!sendMessage(TcpPayload.Ping(targetPeer).asInputStream())) {
//                simpleTcpServer.disconnect(targetPeer)
//                return@launch
//            }
//            delay(TcpConstants.PINGTIMEOUT - TcpConstants.PINGTIME)
//            simpleTcpServer.disconnect(targetPeer)
//        }
//    }

    suspend fun connect(inetSocketAddress: InetSocketAddress): Boolean {
        coroutineScope {
            return@coroutineScope withTimeoutOrNull(CONNECION_TIMEOUT)
            {
                jvmSocket.connect(inetSocketAddress)
                return@withTimeoutOrNull true
            }
        } ?: return false
        return true
    }

//    private suspend fun handlePing() {
//        if (!sendMessage(TcpPayload.Pong(targetPeer).asInputStream())) {
//            simpleTcpServer.disconnect(targetPeer)
//        }
//    }

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
                } while (sizeBuff[offset++] != 0.toByte())
                val test = jvmSocket.getInputStream().read(sizeBuff, 0, 1)
            }
            _clientStateFlow.value = ClientState.RECEIVING_PAYLOAD(0.1f)
            yield()
            val size = ByteBuffer.wrap(sizeBuff).int
            check(size > 0) { "Size is not positive" }
            val byteArray = ByteArray(size)
            var copied = 0
            coroutineScope {
                while (copied < size) {
                    copied += jvmSocket.getInputStream()
                        .read(byteArray, copied, size - copied)
                }
            }
            _clientStateFlow.value = ClientState.RECEIVING_PAYLOAD(0.9f)
            yield()
            return TcpPayload.Pong(targetPeer = Peer.any())
        } catch (e: Exception) {
            _clientStateFlow.value = ClientState.DISCONNECTED.ERROR_WHILE_RECEIVING(e.message ?: "")
            return null
        }
    }
}