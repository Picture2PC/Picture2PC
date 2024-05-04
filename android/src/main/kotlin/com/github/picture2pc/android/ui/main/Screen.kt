package com.github.picture2pc.android.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.camerascreen.CameraScreen
import com.github.picture2pc.android.ui.main.mainscreen.MainScreen
import com.github.picture2pc.android.ui.main.bigpicturescreen.BigPictureScreen
import com.github.picture2pc.android.ui.theme.Picture2PcTheme
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

enum class Screens { MAIN, CAMERA, BIGPICTURE }

@Composable
fun Screen(screenSelector: ScreenSelectorViewModel = rememberKoinInject()) {
    Picture2PcTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (screenSelector.value) {
                Screens.MAIN -> MainScreen()
                Screens.CAMERA -> CameraScreen()
                Screens.BIGPICTURE -> BigPictureScreen()
            }
        }
    }
}