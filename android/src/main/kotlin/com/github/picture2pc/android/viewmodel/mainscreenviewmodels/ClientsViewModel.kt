package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.common.net.defaultdatatransmitter.DefaultDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientsViewModel(
    private val dataTransmitter: DataTransmitter,
    private val viewModelScope: CoroutineScope
) {
    val serverEntries: StateFlow<List<DefaultDevice>> = dataTransmitter.connectedDevices
    
    fun setDeviceCanReceive(deviceUuid: String, canReceive: Boolean) {
        viewModelScope.launch {
            dataTransmitter.setDeviceCanReceive(deviceUuid, canReceive)
        }
    }
}