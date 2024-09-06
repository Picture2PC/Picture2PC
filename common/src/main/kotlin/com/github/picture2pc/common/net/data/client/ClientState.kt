package com.github.picture2pc.common.net.data.client

import androidx.compose.ui.graphics.Color
import com.github.picture2pc.common.ui.StateColors
import jdk.jfr.Percentage

sealed class ClientState(val color: Color, val displayName: String) {
    data object ONLINE :
        ClientState(StateColors.CONNECTED, "Online")

    data object CONNECTED :
        ClientState(StateColors.CONNECTED, "Connected")

    data class SENDING_PAYLOAD(@Percentage val percentage: Float) :
        ClientState(StateColors.RECEIVING, "Sending")

    data class RECEIVING_PAYLOAD(@Percentage val percentage: Float) :
        ClientState(StateColors.RECEIVING, "Receiving")

    sealed class DISCONNECTED(
        private val disconnectColor: Color,
        private val disconnectMessage: String
    ) :
        ClientState(disconnectColor, disconnectMessage) {
        data object NO_ERROR :
            DISCONNECTED(StateColors.DISCONNECTED, "Disconnected")

        data object TIMEOUT :
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