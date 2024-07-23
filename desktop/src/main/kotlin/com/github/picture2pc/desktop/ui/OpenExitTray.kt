package com.github.picture2pc.desktop.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import com.github.picture2pc.common.ui.Picture2PcIcon

@Composable
fun ApplicationScope.OpenExitTray(openAction: () -> Unit, exitAction: () -> Unit) {
    Tray(
        icon = painterResource("icons/icon.svg"),
        onAction = openAction,
        tooltip = "Picture2Pc",
        menu = {
            Item(
                text = "Open",
                onClick = openAction
            )
            Item(
                text = "Exit",
                onClick = exitAction
            )
        }
    )
}
