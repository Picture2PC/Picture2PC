package com.github.picture2pc.common.net2.impl.tcp

import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
) : CoroutineScope //, DefaultDataPayloadTransceiver()
{
    var clientState: ClientState = ClientState.DISCONNECTED

    val socketAddress
        get() = jvmSocket.localSocketAddress as InetSocketAddress


    val isConnected
        get() = jvmSocket.isConnected && !jvmSocket.isClosed

    fun startReceiving() {
        launch {
            while (true) {
                val packet = receivePacket()
                if (packet == null) {
                    simpleTcpServer.disconnect(targetPeer)
                    break
                }
                simpleTcpServer._receivedNetworkPackets.emit(packet)
            }
        }
    }

    suspend fun connect(inetSocketAddress: InetSocketAddress) {
        withContext(Dispatchers.IO) {
            jvmSocket.connect(inetSocketAddress)
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
            jvmSocket.close()
        }
    }

    suspend fun receivePacket(): Payload? {
        try {
            val sizeBuff = ByteArray(Int.SIZE_BYTES)
            withContext(Dispatchers.IO) {
                jvmSocket.getInputStream().read(sizeBuff, 0, Int.SIZE_BYTES)
            }
            val size = ByteBuffer.wrap(sizeBuff).int
            assert(size >= 0) { "Size is negative" }
            return withContext(Dispatchers.IO) {
                val byteArray = ByteArray(size)
                var copied = 0
                while (copied < size) {
                    copied += jvmSocket.getInputStream()
                        .read(byteArray, copied, size - copied)
                }

                return@withContext Payload.fromInputStream(
                    byteArray.inputStream(),
                    InetSocketAddress(jvmSocket.inetAddress, jvmSocket.port)
                )
            }
        } catch (e: Exception) {
            close()
            return null
        }
    }
}