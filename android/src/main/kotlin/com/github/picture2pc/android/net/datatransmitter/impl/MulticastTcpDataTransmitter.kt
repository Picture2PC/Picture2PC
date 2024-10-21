package com.github.picture2pc.android.net.datatransmitter.impl

import com.github.picture2pc.common.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpPayloadTransceiver
import kotlinx.coroutines.CoroutineScope

class MulticastTcpDataTransmitter(
    multicastPayloadTransceiver: MulticastPayloadTransceiver,
    tcpPayloadTransceiver: TcpPayloadTransceiver,
    serverPreferences: ServerPreferencesRepository,
    backgroundScope: CoroutineScope,
) : MulticastTcpDefaultDataTransmitter(
    multicastPayloadTransceiver,
    tcpPayloadTransceiver,
    serverPreferences,
    backgroundScope
)