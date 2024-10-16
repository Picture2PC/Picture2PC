package com.github.picture2pc.desktop.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import com.github.picture2pc.common.ui.Data
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.desktop.ui.util.getIcon

@Composable
fun ApplicationScope.OpenExitTray(openAction: () -> Unit, exitAction: () -> Unit) {
    Tray(
        icon = getIcon(Icons.Logo.STANDARD),
        onAction = openAction,
        tooltip = Data.APP_NAME,
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