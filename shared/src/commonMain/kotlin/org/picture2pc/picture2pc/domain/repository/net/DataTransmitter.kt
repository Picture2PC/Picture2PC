package org.picture2pc.picture2pc.domain.repository.net

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.picture2pc.picture2pc.domain.repository.group.Room
import org.picture2pc.picture2pc.domain.repository.net.client.Client
import org.picture2pc.picture2pc.domain.repository.net.payload.TcpPayload

interface DataTransmitter {
    val availableClients: StateFlow<List<Client>>
    val picture: SharedFlow<TcpPayload.Picture>

    val currentRoom: StateFlow<Room?>

    suspend fun joinRoom(room: Room)
    suspend fun leaveRoom()
    suspend fun sendPicture(picturePayload: TcpPayload.Picture): Boolean
}