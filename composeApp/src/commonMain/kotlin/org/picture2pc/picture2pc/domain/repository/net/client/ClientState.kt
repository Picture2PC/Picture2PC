package org.picture2pc.picture2pc.domain.repository.net.client

import androidx.compose.ui.graphics.Color
import org.picture2pc.picture2pc.presentation.elements.Style
import kotlin.reflect.KClass

sealed class ClientState(val color: Color, val displayName: String) {

    object ONLINE :
        ClientState(Style.Colors.States.CONNECTED, "Online")

    object CONNECTED :
        ClientState(Style.Colors.States.CONNECTED, "Connected")

    data class SENDING_PAYLOAD(val percentage: Float) :
        ClientState(Style.Colors.States.RECEIVING, "Sending")

    data class RECEIVING_PAYLOAD(val payloadType: KClass<*>, val percentage: Float) :
        ClientState(Style.Colors.States.RECEIVING, "Receiving")

    sealed class DISCONNECTED(
        disconnectColor: Color,
        disconnectMessage: String
    ) : ClientState(disconnectColor, disconnectMessage) {
        object NO_ERROR :
            DISCONNECTED(Style.Colors.States.DISCONNECTED, "Disconnected")

        object TIMEOUT :
            DISCONNECTED(Style.Colors.States.DISCONNECTED, "Disconnected due to timeout")

        object ALREADY_CONNECTED :
            DISCONNECTED(Style.Colors.States.DISCONNECTED, "Already connected")

        class OTHER_ERROR(val errorMessage: String) :
            DISCONNECTED(Style.Colors.States.DISCONNECTED, "Disconnected with error")

        class ERROR_WHILE_RECEIVING(val errorMessage: String) :
            DISCONNECTED(
                Style.Colors.States.DISCONNECTED,
                "Disconnected with error while receiving"
            )

        class ERROR_WHILE_SENDING(val errorMessage: String) :
            DISCONNECTED(Style.Colors.States.DISCONNECTED, "Disconnected with error while sending")

        class ERROR_WHILE_CONNECTING(val errorMessage: String) :
            DISCONNECTED(
                Style.Colors.States.DISCONNECTED,
                "Disconnected with error while connecting"
            )
    }
}