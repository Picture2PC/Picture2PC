package com.github.picture2pc.desktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.launchApplication
import com.github.picture2pc.desktop.ui.OpenExitTray
import com.github.picture2pc.desktop.ui.main.MainWindow
import kotlinx.coroutines.CoroutineScope


fun CoroutineScope.launchDesktopApp() = launchApplication {
    var mainWindowVisible by remember { mutableStateOf(true) }

    OpenExitTray(
        openAction = { mainWindowVisible = true },
        exitAction = this::exitApplication
    )

    if (mainWindowVisible) {
        MainWindow(onCloseRequest = { mainWindowVisible = false })
    }
}
