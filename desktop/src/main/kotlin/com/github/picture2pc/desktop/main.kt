package com.github.picture2pc.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.picture2pc.common.ui.Greeting

fun main() = application {
    Window(onCloseRequest = this::exitApplication) {
        Greeting("Desktop")
    }
}