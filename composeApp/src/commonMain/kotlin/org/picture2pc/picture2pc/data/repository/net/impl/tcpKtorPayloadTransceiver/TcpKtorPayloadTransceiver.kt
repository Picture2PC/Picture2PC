package org.picture2pc.picture2pc.data.repository.net.impl.tcpKtorPayloadTransceiver

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.util.network.NetworkAddress
import io.ktor.util.network.address
import io.ktor.util.network.port
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.picture2pc.picture2pc.data.repository.net.payload.Payload
import org.picture2pc.picture2pc.data.repository.net.peer.Peer
import org.picture2pc.picture2pc.domain.repository.net.NetworkPayloadTransceiver

class TcpKtorPayloadTransceiver(val scope: CoroutineScope, ioDispatcher: CoroutineDispatcher) :
    NetworkPayloadTransceiver() {

    private val selectorManager = SelectorManager(ioDispatcher)

    override val available: Boolean = false

    private val peerClientMap = mutableMapOf<Peer, SimpleTcpKtorClient>()
    private val connectedHosts = mutableListOf<String>()


    override suspend fun start() {
        while (kotlin.runCatching {
                val serverSocket = aSocket(selectorManager).tcp().bind()
                scope.launch {
                    while (isActive) {
                        val socket = serverSocket.accept()
                        val hostName = (socket.remoteAddress as InetSocketAddress).hostname
                        if (connectedHosts.contains(hostName)) {
                            socket.close()
                        } else {
                            connectedHosts.add(hostName)
                            runClientLoop(SimpleTcpKtorClient(socket))
                        }
                    }
                }
            }.isFailure) {
            delay(2000)
        }

    }

    suspend fun connect(peer: Peer, networkAddress: NetworkAddress): Boolean {
        if (connectedHosts.contains(networkAddress.address)) {
            return true
        }
        connectedHosts.add(networkAddress.address)
        runCatching {
            val socket =
                aSocket(selectorManager).tcp().connect(networkAddress.address, networkAddress.port)
            val client = SimpleTcpKtorClient(socket, peer)
            runClientLoop(client)
            if (!peer.isAny) {
                peerClientMap[peer] = client
            }
        }.onFailure {
            connectedHosts.remove(networkAddress.address)
            peerClientMap.remove(peer)
            return false
        }
        return true
    }

    fun runClientLoop(client: SimpleTcpKtorClient) {
        client.receivePayloads().onEach {
            receivedPayload(it)
        }.onCompletion {
            connectedHosts.remove(client.connectedAddress?.hostname)
            peerClientMap.remove(client.peer)
        }.launchIn(scope + SupervisorJob())
    }

    override suspend fun _sendPayload(payload: Payload): Boolean {
        if (payload.targetPeer.isAny) {
            return peerClientMap.values.map { scope.async { it.sendPayload(payload) } }.awaitAll()
                .all { true }
        }
        return peerClientMap[payload.targetPeer]?.sendPayload(payload) == true
    }

}