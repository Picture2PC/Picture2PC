package com.github.picture2pc.common.net.defaultdatatransmitter

import com.github.picture2pc.common.net.data.payload.TcpPayload
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface DefaultDataTransmitter {
    val connectedDevices: StateFlow<List<DefaultDevice>>
    val picture: SharedFlow<TcpPayload.Picture>
    val groupInvitations: SharedFlow<Pair<String, TcpPayload.GroupInvitation>>
    suspend fun sendPicture(picturePayload: TcpPayload.Picture): Boolean
    suspend fun setDeviceCanReceive(deviceUuid: String, canReceive: Boolean)
    suspend fun sendGroupInvitation(deviceUuid: String)
    suspend fun respondToGroupInvitation(invitation: TcpPayload.GroupInvitation, accepted: Boolean)
}