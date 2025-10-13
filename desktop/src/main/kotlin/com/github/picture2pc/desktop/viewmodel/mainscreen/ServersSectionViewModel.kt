package com.github.picture2pc.desktop.viewmodel.mainscreen

import com.github.picture2pc.common.net.defaultdatatransmitter.DefaultDevice
import com.github.picture2pc.desktop.net.datatransmitter.DataTransmitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServersSectionViewModel(
    private val dataTransmitter: DataTransmitter,
    private val viewModelScope: CoroutineScope
) {
    val availableServers: StateFlow<List<DefaultDevice>> = dataTransmitter.connectedDevices
    
    fun setDeviceCanReceive(deviceUuid: String, canReceive: Boolean) {
        viewModelScope.launch {
            dataTransmitter.setDeviceCanReceive(deviceUuid, canReceive)
        }
    }
}