package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.android.net.datatransmitter.DefaultDevice
import kotlinx.coroutines.flow.StateFlow

class ClientsViewModel(
    private val dataTransmitter: DataTransmitter
) {
    val serverEntries: StateFlow<List<DefaultDevice>> = dataTransmitter.connectedDevices
}