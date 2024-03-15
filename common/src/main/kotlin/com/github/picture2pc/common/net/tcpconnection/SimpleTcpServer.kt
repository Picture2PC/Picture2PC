package com.github.picture2pc.common.net.tcpconnection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent
import java.net.InetSocketAddress
import kotlin.coroutines.CoroutineContext

class SimpleTcpServer(override val coroutineContext: CoroutineContext) : KoinComponent, CoroutineScope {
    private val jvmServerSocket = java.net.ServerSocket()

    val port
        get() = socketAddress.port
    val socketAddress
        get() = jvmServerSocket.localSocketAddress as InetSocketAddress
    init {
        jvmServerSocket.bind(InetSocketAddress("0.0.0.0", 0))
    }

    suspend fun accept(): SimpleTcpClient {
        val jvmSocket = coroutineScope {
            jvmServerSocket.accept()
        }
        return SimpleTcpClient(coroutineContext, jvmSocket)
    }

}