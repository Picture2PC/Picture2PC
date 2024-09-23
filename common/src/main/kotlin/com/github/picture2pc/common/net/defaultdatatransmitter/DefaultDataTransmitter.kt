package com.github.picture2pc.android.net.datatransmitter

import com.github.picture2pc.common.net.data.payload.TcpPayload
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface DefaultDataTransmitter {
    val connectedDevices: StateFlow<List<DefaultDevice>>
    val pictures: SharedFlow<TcpPayload.Picture>
    suspend fun refreshDevices()
    suspend fun sendPicture(picture: TcpPayload.Picture)
}