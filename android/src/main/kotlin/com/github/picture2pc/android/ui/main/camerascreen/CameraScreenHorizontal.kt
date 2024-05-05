package com.github.picture2pc.android.ui.main.camerascreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview
import com.github.picture2pc.android.ui.main.camerascreen.elements.DisplayPicture
import com.github.picture2pc.android.ui.main.camerascreen.elements.PictureButtons
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun CameraScreenHorizontal(
    cameraViewModel: CameraViewModel = rememberKoinInject(),
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
) {
    val image = cameraViewModel.takenImages.collectAsState(initial = null).value
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max)
                .align(Alignment.Center)
        ) {
            CameraPreview(cameraViewModel = cameraViewModel)
        }
        if (image != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clickable(onClick = screenSelectorViewModel::toBigPicture)
            ) {
                DisplayPicture(image, true)
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            PictureButtons()
        }
    }
}