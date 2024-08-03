package com.github.picture2pc.desktop.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.getIcon
import java.awt.Dimension

@Composable
fun MainWindow(onCloseRequest: () -> Unit) {
    Window(
        onCloseRequest = onCloseRequest,
        icon = getIcon(Icons.Logo.STANDARD),
        title = "Picture2PC",
    ) {
        window.minimumSize = Dimension(1200, 800)
        MainScreen()
    }
}
