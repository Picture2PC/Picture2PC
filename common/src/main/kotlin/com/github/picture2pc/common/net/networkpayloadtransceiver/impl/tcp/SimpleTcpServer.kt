package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp

import com.github.picture2pc.common.net.data.client.ClientState
import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.peer.Peer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.net.InetSocketAddress
import kotlin.coroutines.CoroutineContext

class SimpleTcpServer(override val coroutineContext: CoroutineContext) : CoroutineScope,
    KoinComponent {
    private val jvmServerSocket = java.net.ServerSocket()
    private val peerToClientMap = mutableMapOf<Peer, SimpleTcpClient>()

    val _receivedNetworkPackets = MutableSharedFlow<Payload>()
    val receivedNetworkPackets: SharedFlow<Payload> = _receivedNetworkPackets.asSharedFlow()

    private val _connectedPeers = MutableStateFlow<List<Peer>>(emptyList())
    val connectedPeers: SharedFlow<List<Peer>> = _connectedPeers.asSharedFlow()

    val socketAddress
        get() = jvmServerSocket.localSocketAddress as InetSocketAddress

    init {
        jvmServerSocket.bind(InetSocketAddress("0.0.0.0", 0))
        jvmServerSocket.soTimeout = 1000
    }

    suspend fun accept(peer: Peer): Boolean {
        if (checkPeer(peer)) return false
        val jvmSocket =
            try {
                withTimeout(TcpConstants.CONNECION_TIMEOUT) {
                    jvmServerSocket.accept()
                }
            } catch (e: Exception) {
                return false
            }
        val client = SimpleTcpClient(get(), peer, jvmSocket)
        addPeer(peer, client)
        client.startReceiving()
        return true
    }

    suspend fun connect(peer: Peer, inetSocketAddress: InetSocketAddress): Boolean {
        if (checkPeer(peer)) return false
        val client = SimpleTcpClient(get(), peer)
        if (!coroutineScope {
                return@coroutineScope client.connect(inetSocketAddress)
            }) return false
        addPeer(peer, client)
        client.startReceiving()
        return true
    }

    private fun addPeer(peer: Peer, client: SimpleTcpClient) {
        peerToClientMap[peer] = client
        _connectedPeers.value = peerToClientMap.keys.toList()
        client.clientStateFlow.onEach {
            if (it is ClientState.DISCONNECTED) {
                client.disconnect()
                removePeer(peer)
            }
        }.launchIn(this)
    }

    private fun removePeer(peer: Peer) {
        peerToClientMap.remove(peer)
        _connectedPeers.value = peerToClientMap.keys.toList()
    }

    private fun checkPeer(peer: Peer): Boolean {
        if (peerToClientMap.containsKey(peer)) {
            if (!peerToClientMap[peer]!!.isConnected) {
                removePeer(peer)
                return false
            }
            return true
        }
        return false
    }

    suspend fun sendPayload(payload: Payload): Boolean {
        if (payload.targetPeer.isAny) {
            return coroutineScope {
                val jobs = peerToClientMap.values.map {
                    async {
                        it.sendMessage(payload)
                    }
                }
                jobs.forEach {
                    if (!it.await()) return@coroutineScope false
                }
                return@coroutineScope true
            }
        }
        val client = peerToClientMap.getOrDefault(payload.targetPeer, null) ?: return false
        return client.sendMessage(payload)
    }

    fun getPeerStateAsFlow(peer: Peer): StateFlow<ClientState>? {
        return peerToClientMap.getOrDefault(peer, null)?.clientStateFlow
    }
}