package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp

import com.github.picture2pc.common.net.data.client.ClientState
import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.networkpayloadtransceiver.NetworkPayloadTransceiver
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast.MulticastConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import java.net.InetSocketAddress

class TcpPayloadTransceiver(
    private val backgroundScope: CoroutineScope,
    private val tcpServer: SimpleTcpServer
) : KoinComponent, NetworkPayloadTransceiver() {
    override val available: Boolean
        get() = tcpServer.isAvailable

    override suspend fun start() {
        while (kotlin.runCatching { tcpServer.start() }.isFailure) {
            delay(MulticastConstants.RETRY_DELAY)
        }
    }

    val connectedPeers: StateFlow<List<Peer>>
        get() = tcpServer.connectedPeers

    init {
        tcpServer.receivedNetworkPackets.onEach {
            receivedPayload(it)
        }.launchIn(backgroundScope)
    }

    val inetSocketAddress
        get() = tcpServer.socketAddress

    fun getPeerStateAsStateFlow(peer: Peer): StateFlow<ClientState>? {
        return tcpServer.getPeerStateAsFlow(peer)
    }

    suspend fun connect(peer: Peer, inetSocketAddress: InetSocketAddress? = null): Boolean {
        if (inetSocketAddress == null) {
            return tcpServer.accept(peer)
        }
        return tcpServer.connect(peer, inetSocketAddress)
    }

    override suspend fun _sendPayload(payload: Payload): Boolean {
        return tcpServer.sendPayload(payload)
    }
}