package com.github.picture2pc.common.net2.impl.tcp

import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.impl.common.NetworkPacket
import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
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
        if (!isConnected) return false
        val packet = NetworkPacket(message, socketAddress)
        while (packet.available) {
            val dgPacket = packet.getDatagramPacket()!!
            withContext(Dispatchers.IO) {
                jvmSocket.getOutputStream().write(dgPacket.data, dgPacket.offset, dgPacket.length)
            }
        }
        return true
    }

    suspend fun close() {
        withContext(Dispatchers.IO) {
            jvmSocket.close()
        }
    }

    suspend fun receivePacket(): Payload? {
        val packet = NetworkPacket()
        while (packet.available){
            try {
                val dgPacket = packet.getDatagramPacket()!!
                withContext(Dispatchers.IO) {
                    jvmSocket.getInputStream().read(dgPacket.data, dgPacket.offset, dgPacket.length)
                }
            } catch (e: Exception) {
                close()
                return null
            }
        }
        if (packet.totalSize == 0) {
            close()
            return null
        }
        return Payload.fromInputStream(
            packet.getInputStream(),
            InetSocketAddress(jvmSocket.inetAddress, jvmSocket.port)
        )
    }
}