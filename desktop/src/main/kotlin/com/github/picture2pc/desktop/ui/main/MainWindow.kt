package com.github.picture2pc.desktop.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import com.github.picture2pc.common.ui.Data
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.desktop.viewmodel.mainscreen.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject
import com.github.picture2pc.desktop.ui.getIcon

@Composable
fun MainWindow(
    onCloseRequest: () -> Unit,
    pictureDisplayViewModel: PictureDisplayViewModel = rememberKoinInject(),
) {
    val windowState = rememberWindowState(WindowPlacement.Floating)

    Window(
        onCloseRequest = onCloseRequest,
        icon = getIcon(Icons.Logo.STANDARD),
        title = Data.APP_NAME,
        state = windowState,
    ) {
        window.minimumSize = Data.MINIMUM_WINDOW_SIZE
        MainScreen()

        val isFocused = LocalWindowInfo.current.isWindowFocused
        LaunchedEffect(isFocused) {
            pictureDisplayViewModel.isFocused = isFocused
        }
    }
}