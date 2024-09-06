package com.github.picture2pc.common.net.data.client

import androidx.compose.ui.graphics.Color
import com.github.picture2pc.common.ui.StateColors

sealed class ClientState(val color: Color, val displayName: String) {
    object ONLINE :
        ClientState(StateColors.CONNECTED, "Online")

    object CONNECTED :
        ClientState(StateColors.CONNECTED, "Connected")

    data class SENDING_PAYLOAD(val percentage: Float) :
        ClientState(StateColors.RECEIVING, "Sending")

    data class RECEIVING_PAYLOAD(val percentage: Float) :
        ClientState(StateColors.RECEIVING, "Receiving")

    sealed class DISCONNECTED(
        private val disconnectColor: Color,
        private val disconnectMessage: String
    ) :
        ClientState(disconnectColor, disconnectMessage) {
        object NO_ERROR :
            DISCONNECTED(StateColors.DISCONNECTED, "Disconnected")

        object TIMEOUT :
            DISCONNECTED(StateColors.DISCONNECTED, "Disconnected due to timeout")

        data class OTHER_ERROR(val errorMessage: String) :
            DISCONNECTED(StateColors.DISCONNECTED, "Disconnected with error")

        data class ERROR_WHILE_RECEIVING(val errorMessage: String) :
            DISCONNECTED(StateColors.DISCONNECTED, "Disconnected with error while receiving")

        data class ERROR_WHILE_SENDING(val errorMessage: String) :
            DISCONNECTED(StateColors.DISCONNECTED, "Disconnected with error while sending")

        data class ERROR_WHILE_CONNECTING(val errorMessage: String) :
            DISCONNECTED(StateColors.DISCONNECTED, "Disconnected with error while connecting")
    }
}