package org.picture2pc.picture2pc.domain.repository.net.client

import androidx.compose.ui.graphics.Color
import org.picture2pc.picture2pc.ui.theme.ConnectionColors

sealed class ClientState(val color: Color, val displayName: String) {

    object ONLINE :
        ClientState(ConnectionColors.Connected, "Online")

    object CONNECTED :
        ClientState(ConnectionColors.Connected, "Connected")

    data class SENDING_PAYLOAD(val percentage: Float) :
        ClientState(ConnectionColors.Receiving, "Sending")

    data class RECEIVING_PAYLOAD(val payloadType: String, val percentage: Float) :
        ClientState(ConnectionColors.Receiving, "Receiving")

    sealed class DISCONNECTED(
        disconnectColor: Color,
        disconnectMessage: String
    ) : ClientState(disconnectColor, disconnectMessage) {
        object NO_ERROR :
            DISCONNECTED(ConnectionColors.Disconnected, "Disconnected")

        object TIMEOUT :
            DISCONNECTED(ConnectionColors.Disconnected, "Disconnected due to timeout")

        object ALREADY_CONNECTED :
            DISCONNECTED(ConnectionColors.Disconnected, "Already connected")

        class OTHER_ERROR(val errorMessage: String) :
            DISCONNECTED(ConnectionColors.Disconnected, "Disconnected with error")

        class ERROR_WHILE_RECEIVING(val errorMessage: String) :
            DISCONNECTED(
                ConnectionColors.Disconnected,
                "Disconnected with error while receiving"
            ) {
            override fun toString(): String {
                return errorMessage
            }
        }

        class ERROR_WHILE_SENDING(val errorMessage: String) :
            DISCONNECTED(
                ConnectionColors.Disconnected,
                "Disconnected with error while sending"
            )

        class ERROR_WHILE_CONNECTING(val errorMessage: String) :
            DISCONNECTED(
                ConnectionColors.Disconnected,
                "Disconnected with error while connecting"
            )
    }
}