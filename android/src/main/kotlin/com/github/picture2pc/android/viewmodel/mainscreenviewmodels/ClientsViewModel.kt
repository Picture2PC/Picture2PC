package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.impl.tcp.TcpPayloadTransceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.CoroutineContext

class ClientsViewModel(
    tcpPayloadTransceiver: TcpPayloadTransceiver,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    var serverEntries = MutableStateFlow<List<Peer>>(emptyList()).asStateFlow()

    init {
        serverEntries = tcpPayloadTransceiver.connectedPeers.asStateFlow()
    }
}