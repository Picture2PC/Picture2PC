package com.github.picture2pc.desktop.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import com.github.picture2pc.common.ui.Picture2PcIcon
import java.awt.Dimension

@Composable
fun MainWindow(onCloseRequest: () -> Unit) {
    Window(
        onCloseRequest = onCloseRequest,
        icon = Picture2PcIcon(),
        title = "Picture2PC"
    ) {
        window.minimumSize = Dimension(800, 600)
        MainScreen()
    }
}
