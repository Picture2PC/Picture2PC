package com.github.picture2pc.common.net.tcpconnection

import com.github.picture2pc.common.net.common.NetworkDataPayload
import com.github.picture2pc.common.net.common.ReceivedMulticastPacket
import com.github.picture2pc.common.net2.common.NetworkPacket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalSerializationApi::class)
class SimpleTcpClient(
    override val coroutineContext: CoroutineContext,
    private val jvmSocket: Socket = Socket()
) : CoroutineScope //, DefaultDataPayloadTransceiver()
{

    private val socketAddress
        get() = jvmSocket.localSocketAddress as InetSocketAddress

    val inetAddress
        get() = jvmSocket.inetAddress

    val isConnected
        get() = jvmSocket.isConnected && !jvmSocket.isClosed

    init {
        /*launch {         outgoingPayloads
            .onEach { sendMessage(it) }
            .launchIn(this) }
        if (jvmSocket.isConnected) {
            run()
        }*/
    }

    fun connect(address: String, port: Int) {
        jvmSocket.connect(InetSocketAddress(address, port))
        if (jvmSocket.isConnected) {
            run()
        }
    }

    fun run(){
        launch {
            while (isActive && isConnected) {
                val packet = receivePacket() ?: continue
                val payload = Json.decodeFromStream<NetworkDataPayload>(packet.content)
                // payload.newEvent(payload, packet.address)

            }
        }
    }

    fun sendMessage(message: InputStream) {
        if (!isConnected) return
        val packet = NetworkPacket(message, socketAddress)
        while (packet.available) {
            val dgPacket = packet.getDatagramPacket()!!
            jvmSocket.getOutputStream().write(dgPacket.data, dgPacket.offset, dgPacket.length)
        }
    }

    fun close() {
        jvmSocket.close()
    }

    fun receivePacket(): ReceivedMulticastPacket? {
        val packet = NetworkPacket()
        while (packet.available){
            try {
                val dgPacket = packet.getDatagramPacket()!!
                jvmSocket.getInputStream().read(dgPacket.data, dgPacket.offset, dgPacket.length )
            } catch (e: Exception) {
                close()
                return null
            }
        }
        if (packet.totalSize == 0) {
            close()
            return null
        }
        return ReceivedMulticastPacket(packet.getInputStream(), jvmSocket.inetAddress)
    }
}