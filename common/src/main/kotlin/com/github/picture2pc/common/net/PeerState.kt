package com.github.picture2pc.common.net

import androidx.compose.ui.graphics.Color
import com.github.picture2pc.common.ui.StateColors
import jdk.jfr.Percentage

sealed class PeerState(val color: Color, val displayName: String) {
    data object ONLINE :
        PeerState(StateColors.CONNECTED, "Online")

    data object CONNECTED :
        PeerState(StateColors.CONNECTED, "Connected")

    data class SENDING_PAYLOAD(val percentage: Percentage) :
        PeerState(StateColors.RECEIVING, "Receiving")

    data class RECEIVING_PAYLOAD(val percentage: Percentage) :
        PeerState(StateColors.RECEIVING, "Receiving")

    sealed class DISCONNECTED(
        private val disconnectColor: Color,
        private val disconnectMessage: String
    ) :
        PeerState(disconnectColor, disconnectMessage) {
        data object NO_ERROR :
            DISCONNECTED(StateColors.DISCONNECTED, "Disconnected")

        data class ERROR_WHILE_RECEIVING(val errorMessage: String) :
            DISCONNECTED(StateColors.DISCONNECTED, "Error while receiving")

        data class ERROR_WHILE_SENDING(val errorMessage: String) :
            DISCONNECTED(StateColors.DISCONNECTED, "Error while sending")
    }
}