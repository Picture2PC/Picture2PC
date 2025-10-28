package org.picture2pc.picture2pc.domain.repository.net

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.picture2pc.picture2pc.data.repository.net.payload.TcpPayload
import org.picture2pc.picture2pc.domain.repository.net.client.Client

interface DataTransmitter {
    val connectedClients: StateFlow<List<Client>>
    val picture: SharedFlow<TcpPayload.Picture>
    suspend fun sendPicture(picturePayload: TcpPayload.Picture): Boolean
}