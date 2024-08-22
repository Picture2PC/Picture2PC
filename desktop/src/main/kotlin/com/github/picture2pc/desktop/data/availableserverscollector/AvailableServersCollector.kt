package com.github.picture2pc.desktop.data.availableserverscollector

import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.impl.tcp.ClientState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AvailableServersCollector {
    val availableServers: SharedFlow<List<Peer>>
    fun requestServers()
    fun getClientState(peer: Peer): StateFlow<ClientState>?
}
