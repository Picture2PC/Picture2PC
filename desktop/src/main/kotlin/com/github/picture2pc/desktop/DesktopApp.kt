package com.github.picture2pc.desktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.application
import com.github.picture2pc.desktop.ui.OpenExitTray
import com.github.picture2pc.desktop.ui.main.MainWindow
import kotlin.system.exitProcess

fun runDesktopApp() = application {
    var mainWindowVisible by remember { mutableStateOf(true) }

    OpenExitTray(
        openAction = { mainWindowVisible = true },
        exitAction = { exitApplication(); exitProcess(0) }
    )

    if (mainWindowVisible) {
        MainWindow(onCloseRequest = { mainWindowVisible = false })
    }
}