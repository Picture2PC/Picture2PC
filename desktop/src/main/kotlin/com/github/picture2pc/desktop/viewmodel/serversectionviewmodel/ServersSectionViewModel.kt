package com.github.picture2pc.desktop.viewmodel.serversectionviewmodel

import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.android.net.datatransmitter.DefaultDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServersSectionViewModel(
    private val viewModelScope: CoroutineScope,
    private val dataTransmitter: DataTransmitter,
) {
    val availableServers: StateFlow<List<DefaultDevice>> = dataTransmitter.connectedDevices

    fun refreshServers() {
        viewModelScope.launch {
            dataTransmitter.refreshDevices()
        }
    }
}
