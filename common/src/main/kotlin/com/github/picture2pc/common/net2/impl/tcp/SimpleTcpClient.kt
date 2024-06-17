package com.github.picture2pc.common.net2.impl.tcp

import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.payloads.Payload
import com.github.picture2pc.common.net2.payloads.TcpPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
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
            while (true) {
                val packet = receivePacket()
                if (packet == null) {
                    simpleTcpServer.disconnect(targetPeer)
                    break
                }
                if (packet is TcpPayload.Ping) {
                    handlePing(packet)
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
            Thread.sleep(TcpConstants.PINGTIME)
            if (!sendMessage(TcpPayload.Ping(targetPeer).asInputStream())) {
                simpleTcpServer.disconnect(targetPeer)
                return@launch
            }
            Thread.sleep(TcpConstants.PINGTIMEOUT - TcpConstants.PINGTIME)
            simpleTcpServer.disconnect(targetPeer)
        }
    }

    suspend fun connect(inetSocketAddress: InetSocketAddress): Boolean {
        withContext(Dispatchers.IO) {
            return@withContext withTimeoutOrNull(simpleTcpServer.CONNECION_TIMEOUT)
            {
                jvmSocket.connect(inetSocketAddress)
                return@withTimeoutOrNull true
            }
        } ?: return false
        return true
    }

    suspend fun handlePing(payload: TcpPayload.Ping) {
        if (!sendMessage(TcpPayload.Pong(targetPeer).asInputStream())) {
            simpleTcpServer.disconnect(targetPeer)
        }
    }

    suspend fun sendMessage(message: InputStream): Boolean {
        withContext(Dispatchers.IO) {
            jvmSocket.getOutputStream()
                .write(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(message.available()).array())
            message.copyTo(jvmSocket.getOutputStream())
            message.close()
        }
        return true
    }

    suspend fun close() {
        withContext(Dispatchers.IO) {
            try {
                jvmSocket.close()
            } catch (_: Exception) {

            }
        }
    }

    suspend fun receivePacket(): Payload? {
        try {
            val sizeBuff = ByteArray(Int.SIZE_BYTES)
            clientState.value = ClientState.WAITINGFORDATA
            withContext(Dispatchers.IO) {
                jvmSocket.getInputStream().read(sizeBuff, 0, Int.SIZE_BYTES)
            }
            clientState.value = ClientState.RECEIVING
            val size = ByteBuffer.wrap(sizeBuff).int
            assert(size >= 0) { "Size is negative" }
            return withContext(Dispatchers.IO) {
                val byteArray = ByteArray(size)
                var copied = 0
                while (copied < size) {
                    copied += jvmSocket.getInputStream()
                        .read(byteArray, copied, size - copied)
                }
                clientState.value = ClientState.CONNECTED
                return@withContext Payload.fromInputStream(
                    byteArray.inputStream(),
                    InetSocketAddress(jvmSocket.inetAddress, jvmSocket.port)
                )
            }
        } catch (e: Exception) {
            clientState.value = ClientState.ERRORWHILERECEIVING
            return null
        }
    }


}