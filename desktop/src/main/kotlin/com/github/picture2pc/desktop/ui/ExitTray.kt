package com.github.picture2pc.desktop.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.rememberTrayState
import com.github.picture2pc.common.ui.Data
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.desktop.ui.util.getIcon
import org.koin.core.time.Timer

@Composable
fun ApplicationScope.OpenExitTray(openAction: () -> Unit, exitAction: () -> Unit) {
    val trayState = rememberTrayState()
    trayState.sendNotification(Notification("test", "test2", Notification.Type.Info))
    Tray(
        state = trayState,
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