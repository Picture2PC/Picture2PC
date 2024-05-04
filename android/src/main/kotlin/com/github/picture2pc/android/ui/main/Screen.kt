package com.github.picture2pc.android.ui.main


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.bigpicturescreen.BigPictureScreen
import com.github.picture2pc.android.ui.main.camerascreen.CameraScreen
import com.github.picture2pc.android.ui.main.mainscreen.MainScreenHorizontal
import com.github.picture2pc.android.ui.main.mainscreen.MainScreenVertical
import com.github.picture2pc.android.ui.theme.Picture2PcTheme
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Screen(vertical: Boolean, screenSelector: ScreenSelectorViewModel = rememberKoinInject()) {
    Picture2PcTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            if (vertical) {
                when (screenSelector.value) {
                    ScreenSelectorViewModel.Screens.MAIN -> MainScreenVertical()
                    ScreenSelectorViewModel.Screens.CAMERA -> CameraScreen()
                    ScreenSelectorViewModel.Screens.BIG_PICTURE -> BigPictureScreen()
                }
            } else {
                when (screenSelector.value) {
                    ScreenSelectorViewModel.Screens.MAIN -> MainScreenHorizontal()
                    ScreenSelectorViewModel.Screens.CAMERA -> CameraScreen()
                    ScreenSelectorViewModel.Screens.BIG_PICTURE -> BigPictureScreen()
                }
            }
        }
    }
}