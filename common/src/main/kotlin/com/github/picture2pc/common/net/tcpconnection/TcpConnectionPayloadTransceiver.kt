package com.github.picture2pc.common.net.tcpconnection

import com.github.picture2pc.common.net.common.DefaultDataPayloadTransceiver
import com.github.picture2pc.common.net.common.NetworkDataPayloads
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import kotlin.coroutines.CoroutineContext

class TcpConnectionPayloadTransceiver(private val tcpServer: SimpleTcpServer,
                                      override val coroutineContext: CoroutineContext
): CoroutineScope {

    val clients : HashMap<InetAddress, SimpleTcpClient> = HashMap()
    init {
        NetworkDataPayloads.Ping.incomingPayloads.onEach {
            val client = clients[it.senderAddress] ?: return@onEach
            client.emit(it.payload)
        }.launchIn(this)
        NetworkDataPayloads.ListServers.incomingPayloads.onEach {
            clients[it.senderAddress] = SimpleTcpClient(this.coroutineContext)
            clients[it.senderAddress]!!.emit(NetworkDataPayloads.Ping())
        }.launchIn(this)
        launch {
            while(isActive){
                val client = tcpServer.accept()
                clients[client.inetAddress] = client
            }
        }
    }
    suspend fun send(payload: ByteArray) {
        val client = tcpServer.accept()

    }

    suspend fun receive() {
        val client = tcpServer.accept()
    }

}