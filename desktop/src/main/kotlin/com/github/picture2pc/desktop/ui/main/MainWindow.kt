package com.github.picture2pc.desktop.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import com.github.picture2pc.common.ui.Data
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.getIcon

@Composable
fun MainWindow(onCloseRequest: () -> Unit) {
    Window(
        onCloseRequest = onCloseRequest,
        icon = getIcon(Icons.Logo.STANDARD),
        title = Data.APP_NAME,
    ) {
        window.minimumSize = Data.MINIMUM_WINDOW_SIZE
        MainScreen()
    }
}
