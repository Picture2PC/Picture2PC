package com.github.picture2pc.android.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.bigpicturescreen.BigPictureScreen
import com.github.picture2pc.android.ui.main.camerascreen.CameraScreen
import com.github.picture2pc.android.ui.main.mainscreen.MainScreen
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Style
import org.koin.compose.rememberKoinInject

@Composable
fun Screen(vertical: Boolean, screenSelector: ScreenSelectorViewModel = rememberKoinInject()) {
    val focusManager = LocalFocusManager.current
    Surface(
        color = Colors.BACKGROUND,
        border = BorderStroke(4.dp, Style.Colors.PRIMARY),
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.BACKGROUND)
            .clickable(
                onClick = focusManager::clearFocus,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
    ) {
        when (screenSelector.value) {
            ScreenSelectorViewModel.Screens.MAIN -> MainScreen()
            ScreenSelectorViewModel.Screens.CAMERA -> CameraScreen()
            ScreenSelectorViewModel.Screens.BIG_PICTURE -> BigPictureScreen()
        }
    }
}