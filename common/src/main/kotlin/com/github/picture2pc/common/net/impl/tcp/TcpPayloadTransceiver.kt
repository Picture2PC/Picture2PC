package com.github.picture2pc.common.net.impl.tcp

import com.github.picture2pc.common.net.NetworkPayloadTransceiver
import com.github.picture2pc.common.net.Peer
import com.github.picture2pc.common.net.PeerState
import com.github.picture2pc.common.net.payloads.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
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

    fun getPeerStateAsStateFlow(peer: Peer): StateFlow<PeerState>? {
        return tcpServer.getPeerStateAsFlow(peer)
    }

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