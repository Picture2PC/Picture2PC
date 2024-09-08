package com.github.picture2pc.android.net.datatransmitter

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.skia.Image

interface DefaultDataTransmitter {
    val connectedDevices: StateFlow<List<DefaultDevice>>
    val pictures: SharedFlow<Image>
    suspend fun refreshDevices()
    suspend fun sendPicture(picture: ByteArray)
}