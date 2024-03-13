package com.github.picture2pc.android.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.picture2pc.android.ui.main.camerascreen.CameraScreen
import com.github.picture2pc.android.ui.main.mainscreen.MainScreen
import com.github.picture2pc.android.ui.theme.Picture2PcTheme

enum class Screens { MAIN, CAMERA }

@Preview
@Composable
fun Screen() {
    val currentScreen = remember { mutableStateOf(Screens.MAIN) }

    Picture2PcTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen.value) {
                Screens.MAIN -> MainScreen { currentScreen.value = Screens.CAMERA }
                Screens.CAMERA -> CameraScreen { currentScreen.value = Screens.MAIN }
            }
        }
    }
}