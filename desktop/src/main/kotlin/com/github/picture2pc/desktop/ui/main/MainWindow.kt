package com.github.picture2pc.desktop.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import java.awt.Dimension

@Composable
fun MainWindow(onCloseRequest: () -> Unit) {
    Window(
        onCloseRequest = onCloseRequest
    ) {
        window.minimumSize = Dimension(800, 600)
        MainScreen()
    }
}
