package org.picture2pc.picture2pc.data.repository.net.tcpKtorPayloadTransceiver

import co.touchlab.kermit.Logger
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.port
import io.ktor.util.network.address
import io.ktor.util.network.port
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.picture2pc.picture2pc.data.repository.net.MulticastTcpClient
import org.picture2pc.picture2pc.data.repository.net.common.peer.Peer
import org.picture2pc.picture2pc.domain.repository.PreferencesRepository
import org.picture2pc.picture2pc.domain.repository.group.Room
import org.picture2pc.picture2pc.domain.repository.net.ClientDiscovery
import org.picture2pc.picture2pc.domain.repository.net.DataTransmitter
import org.picture2pc.picture2pc.domain.repository.net.client.ClientSecurityState
import org.picture2pc.picture2pc.domain.repository.net.client.ClientState
import org.picture2pc.picture2pc.domain.repository.net.encryption.EncryptionProvider
import org.picture2pc.picture2pc.domain.repository.net.payload.DiscoverPayload
import org.picture2pc.picture2pc.domain.repository.net.payload.Payload
import org.picture2pc.picture2pc.domain.repository.net.payload.TcpPayload
import kotlin.time.Duration.Companion.milliseconds

class TcpKtorDataTransmitter(
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val clientDiscovery: ClientDiscovery,
    private val encryptionProvider: EncryptionProvider,
    private val serverPreferences: PreferencesRepository,
    private val logger: Logger,
) : DataTransmitter {
    private val _availableClients = MutableStateFlow<List<MulticastTcpClient>>(listOf())
    override val availableClients = _availableClients.asStateFlow()
    private val _picture = MutableSharedFlow<TcpPayload.Picture>()
    override val picture = _picture.asSharedFlow()

    private val _currentRoom = MutableStateFlow<Room?>(null)
    override val currentRoom = _currentRoom.asStateFlow()

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

    override suspend fun joinRoom(room: Room) {
        TODO("Not yet implemented")
    }

    override suspend fun leaveRoom() {
        TODO("Not yet implemented")
    }

    override suspend fun sendPicture(picturePayload: TcpPayload.Picture): Boolean {
        return sendPayload(picturePayload)
    }

    suspend fun start(): Int? {
        while (runCatching {
                val serverSocket = aSocket(selectorManager).tcp().bind()
                scope.launch {
                    while (isActive) {
                        val socket = serverSocket.accept()
                        newConnection(socket)
                    }
                }
                return serverSocket.port
            }.isFailure) {
            delay(2000.milliseconds)
        }
        return null
    }

    private fun restartDiscovery(start: Boolean = true) {
        currentDiscovery?.cancel()
        currentDiscovery = null
        if (start && port != null) {
            currentDiscovery = clientDiscovery.discover {
                DiscoverPayload.ServiceOnline(
                    port!!,
                    serverPreferences.name.value,
                    currentRoom.value,
                    Peer.any()
                )
            }.onEach { payload ->
                newDiscovery(payload)
            }.launchIn(scope)
        }
    }

    private suspend fun newDiscovery(payload: DiscoverPayload.ServiceOnline) {
        if (payload.sourcePeer in peerClientMap.keys) {
            // Update if it is already in availableClients  TODO("DELETE old client and add a new one!")
            val client = peerClientMap[payload.sourcePeer]!!.client
            val state = client._securityState.value as? ClientSecurityState.PeerKnown
            (if (payload.room != null) state?.addRoom(payload.room) else state?.removeRoom())?.let {
                client._securityState.emit(
                    it
                )
            }
            if (client.name.value != payload.peerName)
                client._name.value = payload.peerName
            if (!client.inetSocketAddress.equals(payload.serviceAddress))
                return
        }

        payload.serviceAddress ?: return
        if (hostExists(payload.serviceAddress!!.address))
            return

        val securityState = if (payload.room != null) ClientSecurityState.PeerKnown.UnEncrypted.InRoom(
            payload.room,
            payload.sourcePeer
        ) else ClientSecurityState.PeerKnown.UnEncrypted(payload.sourcePeer)

        val newClient = MulticastTcpClient(
            payload.peerName, ClientState.ONLINE,
            securityState,
            InetSocketAddress(
                payload.serviceAddress!!.address, payload.serviceAddress!!.port
            )
        )

        _availableClients.value += newClient
        if (payload.room == currentRoom.value) {
            // if client is in room connect
            runCatching {
                val socket =
                    aSocket(selectorManager).tcp().connect(
                        newClient.inetSocketAddress.hostname,
                        newClient.inetSocketAddress.port
                    )
                newClient._state.emit(ClientState.CONNECTED)
                val networkClient = SimpleTcpKtorClient(socket, newClient)

                // Add directly
                addPeer(payload.sourcePeer, networkClient)
                runClientLoop(networkClient)
            }
        }
    }

    private suspend fun newConnection(socket: Socket) {
        val hostName = (socket.remoteAddress as InetSocketAddress).hostname
        // Need to check not only if host exists but also if it is connected
        // if the host is not connect I need to keep the MulticastTCPClient and switch the state to connected
        if (hostExists(hostName)) {
            withContext(Dispatchers.IO) {
                socket.close()
            }
        } else {
            val client = MulticastTcpClient.unknown((socket.remoteAddress as InetSocketAddress))
            _availableClients.value += client
            val networkClient = SimpleTcpKtorClient(
                socket,
                client,
            )

            // Add only after validating to peerClientMap
            client.securityState.filterIsInstance<ClientSecurityState.PeerKnown>().onEach {
                if (addPeer(it.peer, networkClient)) {
                    networkClient.sendPayload(TcpPayload.RequestName(it.peer))
                    networkClient.sendPayload(TcpPayload.EncryptChannel(it.peer))
                }
            }.take(1).launchIn(scope)

            runClientLoop(networkClient)
        }
    }

    private fun hostExists(address: String) =
        availableClients.value.any { it.inetSocketAddress.hostname == address }


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
                    if (client.client.securityState is ClientSecurityState.PeerKnown.UnEncrypted)
                        client.client._securityState.emit(
                            client.client.securityState.addEncryption(sharedKey)
                        )
                }
            }

            is TcpPayload.EncryptOk -> {
                val sharedKey = encryptionProvider.getSharedKey(payload.sourcePeer)
                if (sharedKey != null && client.client.securityState is ClientSecurityState.PeerKnown.UnEncrypted) {
                    client.client._securityState.emit(
                        client.client.securityState.addEncryption(sharedKey)
                    )
                }
            }

            is TcpPayload.RoomChange -> {
                if (client.client.securityState is ClientSecurityState.PeerKnown)
                    client.client._securityState.emit(
                        client.client.securityState.addRoom(payload.room)
                    )
            }

            else -> {}
        }
    }

    private fun runClientLoop(client: SimpleTcpKtorClient) {
        // Handle all payloads
        client.receivePayloads().onEach {
            scope.launch {
                handlePayload(client, payload = it)
            }
        }.onCompletion {
            (client.client.securityState.value as? ClientSecurityState.PeerKnown)?.let {
                peerClientMap.remove(it.peer)
            }
            _availableClients.value -= client.client
        }.launchIn(scope)

        // Ping every 3 Seconds
        scope.launch {
            while (!client.isClosed) {
                client.sendPayload(TcpPayload.Ping(Peer.any()))
                delay(3000.milliseconds)
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