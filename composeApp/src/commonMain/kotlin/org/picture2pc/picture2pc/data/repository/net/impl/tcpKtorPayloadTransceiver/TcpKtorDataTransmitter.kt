package org.picture2pc.picture2pc.data.repository.net.impl.tcpKtorPayloadTransceiver

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.picture2pc.picture2pc.data.repository.net.impl.MulticastTcpClient
import org.picture2pc.picture2pc.data.repository.net.payload.DiscoverPayload
import org.picture2pc.picture2pc.data.repository.net.payload.Payload
import org.picture2pc.picture2pc.data.repository.net.payload.TcpPayload
import org.picture2pc.picture2pc.data.repository.net.peer.Peer
import org.picture2pc.picture2pc.domain.repository.EncryptionProvider
import org.picture2pc.picture2pc.domain.repository.PreferencesRepository
import org.picture2pc.picture2pc.domain.repository.net.ClientDiscovery
import org.picture2pc.picture2pc.domain.repository.net.DataTransmitter
import org.picture2pc.picture2pc.domain.repository.net.client.ClientSecurityState
import org.picture2pc.picture2pc.domain.repository.net.client.ClientState

class TcpKtorDataTransmitter(
    val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val clientDiscovery: ClientDiscovery,
    private val encryptionProvider: EncryptionProvider,
    private val serverPreferences: PreferencesRepository
) : DataTransmitter {
    private val _connectedClients = MutableStateFlow<List<MulticastTcpClient>>(listOf())
    override val connectedClients = _connectedClients.asStateFlow()
    private val _picture = MutableSharedFlow<TcpPayload.Picture>()
    override val picture = _picture.asSharedFlow()
    override suspend fun sendPicture(picturePayload: TcpPayload.Picture): Boolean {
        return sendPayload(picturePayload)
    }


    private var currentDiscovery: Job? = null
    private var port: Int? = null

    private val selectorManager = SelectorManager(ioDispatcher)

    private val peerClientMap = mutableMapOf<Peer, SimpleTcpKtorClient>()

    init {
        scope.launch {
            port = start()
            restartDiscovery(true)
            serverPreferences.name.onEach {
                sendPayload(TcpPayload.NameUpdate(it, Peer.any()))
            }.launchIn(scope)
            serverPreferences.connectable.onEach {
                restartDiscovery(it)
            }.launchIn(scope)
        }
    }

    private fun restartDiscovery(start: Boolean = true) {
        currentDiscovery?.cancel()
        currentDiscovery = null
        if (start && port != null) {
            currentDiscovery = clientDiscovery.discover {
                DiscoverPayload.ServiceOnline(
                    port!!,
                    serverPreferences.name.value,
                    Peer.any()
                )
            }.onEach { payload ->
                payload.serviceAddress ?: return@onEach
                if (hostExists(payload.serviceAddress!!.address))
                    return@onEach
                val newClient = MulticastTcpClient(
                    payload.peerName, ClientState.ONLINE,
                    ClientSecurityState.PeerKnown.UnEncrypted(payload.sourcePeer), InetSocketAddress(
                        payload.serviceAddress!!.address, payload.serviceAddress!!.port
                    )
                )
                connect(newClient)
            }.launchIn(scope)
        }
    }


    fun hostExists(address: String) =
        connectedClients.value.any { it.inetSocketAddress.hostname == address }

    suspend fun start(): Int? {
        while (kotlin.runCatching {
                require(!Peer.getSelf().isAny) // BLOCK until peer UUID is loaded
                val serverSocket = aSocket(selectorManager).tcp().bind()
                scope.launch {
                    while (isActive) {
                        val socket = serverSocket.accept()
                        val hostName = (socket.remoteAddress as InetSocketAddress).hostname
                        if (hostExists(hostName)) {
                            socket.close()
                        } else {
                            runClientLoop(
                                SimpleTcpKtorClient(
                                    socket,
                                    MulticastTcpClient.unknown((socket.remoteAddress as InetSocketAddress))
                                )
                            )
                        }
                    }
                }
                return serverSocket.port
            }.isFailure) {
            delay(2000)
        }
        return null
    }

    private suspend fun connect(multicastTcpClient: MulticastTcpClient) {
        runCatching {
            val socket =
                aSocket(selectorManager).tcp().connect(
                    multicastTcpClient.inetSocketAddress.hostname,
                    multicastTcpClient.inetSocketAddress.port
                )
            multicastTcpClient._state.emit(ClientState.CONNECTED)
            val client = SimpleTcpKtorClient(socket, multicastTcpClient)
            runClientLoop(client)
        }
    }

    private suspend fun handlePayload(client: SimpleTcpKtorClient, payload: Payload) {
        when (payload) {
            is TcpPayload.NameUpdate -> client.client._name.emit(payload.name)
            is TcpPayload.Picture -> _picture.emit(payload)
            is TcpPayload.Ping -> client.sendPayload(TcpPayload.Pong(payload.sourcePeer))
            is TcpPayload.Pong -> null
            is TcpPayload.RequestName -> client.sendPayload(
                TcpPayload.NameUpdate(
                    serverPreferences.name.value,
                    payload.sourcePeer
                )
            )
            is TcpPayload.EncryptChannel -> {
                val sharedKey = encryptionProvider.getSharedKey(payload.sourcePeer)
                if (sharedKey != null) {
                    client.sendPayload(TcpPayload.EncryptOk(payload.sourcePeer))
                    client.client._securityState.emit(
                        ClientSecurityState.PeerKnown.Encrypted(
                            payload.sourcePeer,
                            sharedKey
                        )
                    )
                }
            }

            is TcpPayload.EncryptOk -> {
                val sharedKey = encryptionProvider.getSharedKey(payload.sourcePeer)
                if (sharedKey != null) {
                    client.client._securityState.emit(
                        ClientSecurityState.PeerKnown.Encrypted(
                            payload.sourcePeer,
                            sharedKey
                        )
                    )
                }
            }
            else -> {}
        }
    }

    private suspend fun runClientLoop(client: SimpleTcpKtorClient) {
        _connectedClients.value += client.client

        client.receivePayloads().onEach {
            scope.launch {
                handlePayload(client, payload = it)
            }
        }.onCompletion {
            (client.client.securityState.value as? ClientSecurityState.PeerKnown)?.let {
                peerClientMap.remove(it.peer)
            }
            _connectedClients.value -= client.client
        }.launchIn(scope)

        if (client.client.securityState.value is ClientSecurityState.PeerKnown) {
            val peer = (client.client.securityState.value as ClientSecurityState.PeerKnown).peer
            addPeer(peer, client)
        }
        else {
            client.client.securityState.filterIsInstance<ClientSecurityState.PeerKnown>().onEach {
                if (addPeer(it.peer, client)) {
                    client.sendPayload(TcpPayload.RequestName(it.peer))
                    client.sendPayload(TcpPayload.EncryptChannel(it.peer))
                }

            }.take(1).launchIn(scope)
        }
        scope.launch {
            while (!client.isClosed) {
                client.sendPayload(TcpPayload.Ping(Peer.any()))
                delay(3000)
            }
        }
    }

    private suspend fun addPeer(peer: Peer, client: SimpleTcpKtorClient): Boolean {
        val current = peerClientMap[peer]
        if (current != null) {
            if (peer.uuid.hashCode() > Peer.getSelf().uuid.hashCode()) {
                client.close(ClientState.DISCONNECTED.ALREADY_CONNECTED)
                return false
            } else {
                current.close(ClientState.DISCONNECTED.ALREADY_CONNECTED)
            }
        }
        peerClientMap[peer] = client
        return true
    }

    private suspend fun sendPayload(payload: Payload): Boolean {
        if (payload.targetPeer.isAny) {
            return peerClientMap.values.map { scope.async { it.sendPayload(payload) } }.awaitAll()
                .all { true }
        }
        return peerClientMap[payload.targetPeer]?.sendPayload(payload) == true
    }

}