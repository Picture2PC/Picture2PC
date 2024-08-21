package com.github.picture2pc.desktop.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.github.picture2pc.common.ui.Data
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.desktop.ui.util.getIcon
import java.awt.Toolkit

@Composable
fun MainWindow(onCloseRequest: () -> Unit) {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowWidth = Data.MINIMUM_WINDOW_SIZE.width
    val windowHeight = Data.MINIMUM_WINDOW_SIZE.height
    val centerWindowState = rememberWindowState(
        position = WindowPosition(
            x = ((screenSize.width - windowWidth) / 2).dp,
            y = ((screenSize.height - windowHeight) / 2).dp
        ),
        size = DpSize(windowWidth.dp, windowHeight.dp)
    )

    Window(
        onCloseRequest = onCloseRequest,
        icon = getIcon(Icons.Logo.STANDARD),
        title = Data.APP_NAME,
        state = centerWindowState
    ) {
        window.minimumSize = Data.MINIMUM_WINDOW_SIZE
        MainScreen()
    }
}
