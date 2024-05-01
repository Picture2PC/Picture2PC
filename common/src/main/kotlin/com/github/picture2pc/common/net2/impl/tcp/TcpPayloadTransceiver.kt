package com.github.picture2pc.common.net2.impl.tcp

import com.github.picture2pc.common.net2.NetworkPayloadTransceiver
import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.net.InetSocketAddress
import kotlin.coroutines.CoroutineContext

class TcpPayloadTransceiver(override val coroutineContext: CoroutineContext) : CoroutineScope,
    KoinComponent,
    NetworkPayloadTransceiver() {

    private val tcpServer: SimpleTcpServer = get()
    val connectedPeers
        get() = tcpServer.connectedPeers

    init {
        tcpServer.receivedNetworkPackets.onEach {
            receivedPayload(it)
        }.launchIn(this)
    }


    val inetSocketAddress
        get() = tcpServer.socketAddress


    suspend fun connect(peer: Peer, inetSocketAddress: InetSocketAddress? = null) {
        if (inetSocketAddress == null) {
            tcpServer.accept(peer)
            return
        }
        tcpServer.connect(peer, inetSocketAddress)
    }

    override suspend fun _sendPayload(payload: Payload): Boolean {
        return tcpServer.sendPayload(payload)
    }
}