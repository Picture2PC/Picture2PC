package com.github.picture2pc.desktop.net.datatransmitter.impl

import com.github.picture2pc.common.data.preferences.PreferencesRepository
import com.github.picture2pc.common.net.defaultdatatransmitter.impl.MulticastTcpDefaultDataTransmitter
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpPayloadTransceiver
import com.github.picture2pc.desktop.net.datatransmitter.DataTransmitter
import kotlinx.coroutines.CoroutineScope

class MulticastTcpDataTransmitter(
    multicastPayloadTransceiver: MulticastPayloadTransceiver,
    tcpPayloadTransceiver: TcpPayloadTransceiver,
    serverPreferences: PreferencesRepository,
    backgroundScope: CoroutineScope,
) : DataTransmitter, MulticastTcpDefaultDataTransmitter(
    multicastPayloadTransceiver,
    tcpPayloadTransceiver,
    serverPreferences,
    backgroundScope
)