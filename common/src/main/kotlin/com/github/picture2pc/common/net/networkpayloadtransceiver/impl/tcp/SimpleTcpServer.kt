package com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp

import com.github.picture2pc.common.net.data.client.ClientState
import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpConstants.CONNECION_TIMEOUT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val _receivedNetworkPackets = MutableSharedFlow<Payload>(0, 1)
    val receivedNetworkPackets: SharedFlow<Payload> = _receivedNetworkPackets.asSharedFlow()

    private val lock = Mutex()

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
        backgroundScope.launch {
            while (backgroundScope.isActive) {
                if (!isAvailable) delay(1000)
                kotlin.runCatching { accept() }
            }
        }
    }

    private suspend fun accept(): Boolean {
        if (!isAvailable) return false
        val jvmSocket =
            try {
                withTimeout(CONNECION_TIMEOUT) {
                    jvmServerSocket.accept()
                }
            } catch (e: Exception) {
                return false
            }
        val client = SimpleTcpClient(backgroundScope + Job(), ioDispatcher, jvmSocket)
        addPeer(client)
        return true
    }

    suspend fun connect(peer: Peer, inetSocketAddress: InetSocketAddress): Boolean {
        lock.withLock {
            if (!isAvailable || checkPeer(peer)) return false
        }
        val client = SimpleTcpClient(
            backgroundScope + Job(),
            ioDispatcher,
            withContext(ioDispatcher) { java.net.Socket() })
        lock.withLock {
            tryAddPeer(peer, client)
        }
        val res = client.connect(inetSocketAddress)
        if (res)
            addPeer(client)
        else
            lock.withLock { if (peerToClientMap[peer] == client) removePeer(peer) }
        return res
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

    private fun addPeer(client: SimpleTcpClient) {
        println("This is: ${Peer.getSelf()}")
        client.clientStateFlow.onEach {
            println("Peer ${client.peer} state: $it")
        }.launchIn(backgroundScope)
        backgroundScope.launch {
            client.startTimeout()
            var f = true
            client.startListen().collect {
                if (f) {
                    f = false
                    if (!tryAddPeer(client.peer, client)) {
                        if (client.isServer == client.peer.uuid.hashCode() < Peer.getSelf().uuid.hashCode())
                            client.disconnect(ClientState.DISCONNECTED.NO_ERROR)
                        else {
                            lock.withLock {
                                peerToClientMap[client.peer]?.disconnect(ClientState.DISCONNECTED.NO_ERROR)
                                peerToClientMap[client.peer] = client
                                _connectedPeers.emit(peerToClientMap.keys.toList())
                            }
                        }
                    }
                }
                _receivedNetworkPackets.emit(it)
            }
            lock.withLock {
                if (peerToClientMap[client.peer] == client)
                removePeer(client.peer)
            }
        }
    }

    private suspend fun tryAddPeer(peer: Peer, client: SimpleTcpClient): Boolean {
        if (checkPeer(peer))
            return peerToClientMap[peer] == client
        peerToClientMap[peer] = client
        _connectedPeers.emit(peerToClientMap.keys.toList())
        return true
    }

    private suspend fun removePeer(peer: Peer) {
        peerToClientMap.remove(peer)
        _connectedPeers.emit(peerToClientMap.keys.toList())
    }

    private fun checkPeer(peer: Peer?): Boolean {
        if (peer == null) return false
        return peerToClientMap.containsKey(peer)
    }

}