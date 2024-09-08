package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp

import com.github.picture2pc.common.net.data.client.ClientState
import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.peer.Peer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import java.net.InetSocketAddress
import kotlin.collections.set

class SimpleTcpServer(
    private val backgroundScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher
) :
    KoinComponent {
    private lateinit var jvmServerSocket: java.net.ServerSocket
    private val peerToClientMap = mutableMapOf<Peer, SimpleTcpClient>()

    val _receivedNetworkPackets = MutableSharedFlow<Payload>(0, 1)
    val receivedNetworkPackets: SharedFlow<Payload> = _receivedNetworkPackets.asSharedFlow()

    private val _connectedPeers = MutableStateFlow<List<Peer>>(emptyList())
    val connectedPeers: StateFlow<List<Peer>> = _connectedPeers.asStateFlow()

    val isAvailable: Boolean
        get() = this::jvmServerSocket.isInitialized && jvmServerSocket.isBound && !jvmServerSocket.isClosed

    val socketAddress
        get() = jvmServerSocket.localSocketAddress as InetSocketAddress


    suspend fun start() {
        withContext(ioDispatcher)
        {
            jvmServerSocket = java.net.ServerSocket()
            jvmServerSocket.bind(InetSocketAddress("0.0.0.0", 0))
            jvmServerSocket.soTimeout = 1000
        }
    }


    suspend fun accept(peer: Peer): Boolean {
        if (!isAvailable || checkPeer(peer)) return false
        val jvmSocket =
            try {
                withTimeout(CONNECION_TIMEOUT) {
                    jvmServerSocket.accept()
                }
            } catch (e: Exception) {
                return false
            }
        val client = SimpleTcpClient(backgroundScope, ioDispatcher, peer, jvmSocket)
        addPeer(peer, client)
        client.startReceiving()
        return true
    }

    suspend fun connect(peer: Peer, inetSocketAddress: InetSocketAddress): Boolean {
        if (!isAvailable || checkPeer(peer)) return false
        val client = SimpleTcpClient(backgroundScope, ioDispatcher, peer)
        if (!coroutineScope {
                return@coroutineScope client.connect(inetSocketAddress)
            }) return false
        addPeer(peer, client)
        client.startReceiving()
        return true
    }

    suspend fun sendPayload(payload: Payload): Boolean {
        if (!isAvailable) return false
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

    private fun addPeer(peer: Peer, client: SimpleTcpClient) {
        peerToClientMap[peer] = client
        _connectedPeers.value = peerToClientMap.keys.toList()
        client.clientStateFlow.onEach {
            if (it is ClientState.DISCONNECTED) {
                client.disconnect()
                removePeer(peer)
            }
        }.launchIn(backgroundScope)
        client.receivedPayloads.onEach {
            _receivedNetworkPackets.tryEmit(it)
        }.launchIn(backgroundScope)
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

}