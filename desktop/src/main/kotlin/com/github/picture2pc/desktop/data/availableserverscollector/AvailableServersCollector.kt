package com.github.picture2pc.desktop.data.availableserverscollector

import com.github.picture2pc.common.net.Peer
import com.github.picture2pc.common.net.PeerState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AvailableServersCollector {
    val availableServers: SharedFlow<List<Peer>>
    fun requestServers()
    fun getClientState(peer: Peer): StateFlow<PeerState>?
}
