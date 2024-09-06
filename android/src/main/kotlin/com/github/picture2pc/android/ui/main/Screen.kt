package com.github.picture2pc.android.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.bigpicturescreen.BigPictureScreen
import com.github.picture2pc.android.ui.main.camerascreen.CameraScreen
import com.github.picture2pc.android.ui.main.mainscreen.MainScreen
import com.github.picture2pc.android.ui.theme.P2PC_Mobile_Theme
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Screen(vertical: Boolean, screenSelector: ScreenSelectorViewModel = rememberKoinInject()) {
    P2PC_Mobile_Theme {
        Surface(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(10.dp)
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(25.dp))
        ) {
            when (screenSelector.value) {
                ScreenSelectorViewModel.Screens.MAIN -> MainScreen()
                ScreenSelectorViewModel.Screens.CAMERA -> CameraScreen()
                ScreenSelectorViewModel.Screens.BIG_PICTURE -> BigPictureScreen()
            }
        }
    }
}