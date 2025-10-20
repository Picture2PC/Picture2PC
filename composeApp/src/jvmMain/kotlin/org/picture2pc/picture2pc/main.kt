package org.picture2pc.picture2pc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.picture2pc.picture2pc.di.initKoin
import org.picture2pc.picture2pc.presentation.ui.App

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Picture2PC",
    ) {
        App()
    }
}