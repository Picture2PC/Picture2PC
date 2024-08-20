package com.github.picture2pc.common.net2.impl.tcp

import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.payloads.Payload
import com.github.picture2pc.common.net2.payloads.TcpPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    }

    suspend fun accept(peer: Peer): Boolean {
        if (checkPeer(peer)) return false
        val jvmSocket = coroutineScope {
            try {
                jvmServerSocket.accept()
            } catch (e: Exception) {
                return@coroutineScope null
            }
        } ?: return false
        val client = SimpleTcpClient(get(), peer, this, jvmSocket)
        client.clientState = ClientState.PENDING
        addPeer(peer, client)
        val packet = client.receivePacket()
        if (packet == null || packet !is TcpPayload.Ping) {
            disconnect(peer)
            return false
        }
        if (!sendPayload(TcpPayload.Pong(peer))) {
            disconnect(peer)
            return false
        }
        client.clientState = ClientState.CONNECTED
        client.startReceiving()
        return true
    }

    suspend fun connect(peer: Peer, inetSocketAddress: InetSocketAddress): Boolean {
        if (checkPeer(peer)) return false
        val client = SimpleTcpClient(get(), peer, this)
        coroutineScope {
            client.connect(inetSocketAddress)
        }
        client.clientState = ClientState.PENDING
        addPeer(peer, client)

        if (!sendPayload(TcpPayload.Ping(peer))) {
            disconnect(peer)
            return false
        }

        val packet = client.receivePacket()
        if (packet == null || packet !is TcpPayload.Pong) {
            disconnect(peer)
            return false
        }
        client.clientState = ClientState.CONNECTED
        client.startReceiving()
        return true
    }

    private fun addPeer(peer: Peer, client: SimpleTcpClient) {
        peerToClientMap[peer] = client
        _connectedPeers.value = peerToClientMap.keys.toList()
    }

    private fun removePeer(peer: Peer) {
        peerToClientMap.remove(peer)
        _connectedPeers.value = peerToClientMap.keys.toList()
    }

    private suspend fun checkPeer(peer: Peer): Boolean {
        if (peerToClientMap.containsKey(peer)) {
            if (!peerToClientMap[peer]!!.isConnected) {
                disconnect(peer)
                return false
            }
            return true
        }
        return false
    }

    suspend fun disconnect(peer: Peer) {
        val client = peerToClientMap.getOrDefault(peer, null) ?: return
        client.clientState = ClientState.DISCONNECTED
        client.close()
        removePeer(peer)
    }

    suspend fun sendPayload(payload: Payload): Boolean {
        if (payload.targetPeer.isAny) {
            return coroutineScope {
                val jobs = peerToClientMap.values.map {
                    async {
                        it.sendMessage(payload.asInputStream())
                    }
                }
                jobs.forEach {
                    if (!it.await()) return@coroutineScope false
                }
                return@coroutineScope true
            }
        }
        val client = peerToClientMap.getOrDefault(payload.targetPeer, null) ?: return false
        return client.sendMessage(payload.asInputStream())
    }


}