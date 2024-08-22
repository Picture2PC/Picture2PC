package com.github.picture2pc.common.net2.impl.tcp

import androidx.compose.ui.graphics.Color
import com.github.picture2pc.common.ui.StateColors

enum class ClientState(val color: Color, val displayName: String) {
    PENDING(StateColors.PENDING, "Pending"),
    CONNECTED(StateColors.CONNECTED, "Connected"),
    WAITING_FOR_DATA(StateColors.WAITING_FOR_DATA, "Waiting for data"),
    RECEIVING(StateColors.RECEIVING, "Receiving"),
    DISCONNECTED(StateColors.DISCONNECTED, "Disconnected"),
    ERROR_WHILE_RECIEVING(StateColors.ERROR_WHILE_RECIEVING, "Error while receiving"),
    ERROR_WHILE_SENDING(StateColors.ERROR_WHILE_SENDING, "Error while sending"),
}