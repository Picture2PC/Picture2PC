package com.github.picture2pc.android.ui.main.camerascreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview
import com.github.picture2pc.android.ui.main.camerascreen.elements.DisplayImage
import com.github.picture2pc.android.ui.main.camerascreen.elements.PictureButtons
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel = rememberKoinInject()
){
    var alpha = .5f
    val lastImage = cameraViewModel.takenImages.collectAsState(initial = cameraViewModel.getBlankImage())
    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .height(IntrinsicSize.Max)
            .width(IntrinsicSize.Max)
            .align(Alignment.Center)
        ) {
            CameraPreview(cameraViewModel = cameraViewModel)
        }
        Box (modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            PictureButtons()
        }
        if (lastImage.value.byteCount == 4){ alpha = .0f }
        Box(modifier = Modifier
            .align(Alignment.TopStart)
        ) {
            DisplayImage(image = lastImage.value, alpha)
        }
    }
}