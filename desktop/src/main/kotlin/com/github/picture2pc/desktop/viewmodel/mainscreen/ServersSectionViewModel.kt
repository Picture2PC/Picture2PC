package com.github.picture2pc.desktop.viewmodel.mainscreen

import com.github.picture2pc.android.net.datatransmitter.DefaultDevice
import com.github.picture2pc.desktop.net.datatransmitter.DataTransmitter
import kotlinx.coroutines.flow.StateFlow

class ServersSectionViewModel(
    dataTransmitter: DataTransmitter,
) {
    val availableServers: StateFlow<List<DefaultDevice>> = dataTransmitter.connectedDevices
}