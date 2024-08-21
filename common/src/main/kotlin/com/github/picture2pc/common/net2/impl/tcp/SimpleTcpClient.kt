package com.github.picture2pc.common.net2.impl.tcp

import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.payloads.Payload
import com.github.picture2pc.common.net2.payloads.TcpPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalSerializationApi::class)
class SimpleTcpClient(
    override val coroutineContext: CoroutineContext,
    val targetPeer: Peer,
    private val simpleTcpServer: SimpleTcpServer,
    private val jvmSocket: Socket = Socket(),
    private var timeoutJob: Job? = null
) : CoroutineScope //, DefaultDataPayloadTransceiver()
{
    var clientState: MutableStateFlow<ClientState> = MutableStateFlow(ClientState.DISCONNECTED)

    val socketAddress
        get() = jvmSocket.localSocketAddress as InetSocketAddress


    val isConnected
        get() = jvmSocket.isConnected && !jvmSocket.isClosed

    fun startReceiving() {
        timeoutJob = getTimeoutJob()
        launch {
            while (isActive) {
                val packet = receivePacket()
                if (packet == null) {
                    simpleTcpServer.disconnect(targetPeer)
                    break
                }
                if (packet is TcpPayload.Ping) {
                    handlePing()
                    continue
                }
                simpleTcpServer._receivedNetworkPackets.emit(packet)
            }
        }

        clientState.onEach {
            if (it == ClientState.RECEIVING) {
                timeoutJob!!.cancelAndJoin()
                timeoutJob = getTimeoutJob()
            }
        }.launchIn(this)
    }

    private fun getTimeoutJob(): Job {
        return launch {
            delay(TcpConstants.PINGTIME)
            if (!sendMessage(TcpPayload.Ping(targetPeer).asInputStream())) {
                simpleTcpServer.disconnect(targetPeer)
                return@launch
            }
            delay(TcpConstants.PINGTIMEOUT - TcpConstants.PINGTIME)
            simpleTcpServer.disconnect(targetPeer)
        }
    }

    suspend fun connect(inetSocketAddress: InetSocketAddress): Boolean {
        coroutineScope {
            return@coroutineScope withTimeoutOrNull(TcpConstants.CONNECION_TIMEOUT)
            {
                jvmSocket.connect(inetSocketAddress)
                return@withTimeoutOrNull true
            }
        } ?: return false
        return true
    }

    private suspend fun handlePing() {
        if (!sendMessage(TcpPayload.Pong(targetPeer).asInputStream())) {
            simpleTcpServer.disconnect(targetPeer)
        }
    }

    suspend fun sendMessage(message: InputStream): Boolean {
        return coroutineScope {
            try {
                jvmSocket.getOutputStream()
                    .write(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(message.available()).array())
                message.copyTo(jvmSocket.getOutputStream())
            } catch (e: Exception) {
                clientState.value = ClientState.ERROR_WHILE_SENDING
                simpleTcpServer.disconnect(targetPeer)
                message.close()
                return@coroutineScope false
            }
            message.close()
            return@coroutineScope true
        }
    }

    suspend fun close() {
        coroutineScope {
            try {
                jvmSocket.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun receivePacket(): Payload? {
        try {
            val sizeBuff = ByteArray(Int.SIZE_BYTES)
            clientState.emit(ClientState.WAITING_FOR_DATA)
            coroutineScope {
                jvmSocket.getInputStream().read(sizeBuff, 0, Int.SIZE_BYTES)
            }
            clientState.emit(ClientState.RECEIVING)
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
            clientState.emit(ClientState.CONNECTED)
            yield()
            return Payload.fromInputStream(
                byteArray.inputStream(),
                InetSocketAddress(jvmSocket.inetAddress, jvmSocket.port)
            )
        } catch (e: Exception) {
            clientState.emit(ClientState.ERROR_WHILE_RECIEVING)
            return null
        }
    }


}