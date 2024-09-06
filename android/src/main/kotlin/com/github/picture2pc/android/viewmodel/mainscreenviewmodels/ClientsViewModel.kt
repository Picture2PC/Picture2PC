package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpPayloadTransceiver
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class ClientsViewModel(
    tcpPayloadTransceiver: TcpPayloadTransceiver,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    val serverEntries = tcpPayloadTransceiver.connectedPeers
}