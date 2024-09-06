package com.github.picture2pc.desktop.viewmodel.serversectionviewmodel

import com.github.picture2pc.common.net.data.client.ClientState
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.desktop.data.availableserverscollector.AvailableServersCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

class ServersSectionViewModel(
    private val availableServersCollector: AvailableServersCollector,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    private val _availablePeers: MutableStateFlow<List<ServerEntryState>> =
        MutableStateFlow(mutableListOf())
    val availableServers = _availablePeers.asStateFlow()

    init {
        availableServersCollector.availableServers
            .onEach {
                _availablePeers.value = (it.map { peer ->
                    ServerEntryState(
                        peer,
                        peer.name,
                        availableServersCollector.getClientState(peer)
                            ?: return@onEach
                    )
                })
            }.launchIn(this)
    }

    fun refreshServers() {
        availableServersCollector.requestServers()
    }

    data class ServerEntryState(
        val peer: Peer,
        val deviceName: String,
        val connectionState: StateFlow<ClientState>
    )
}
