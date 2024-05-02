package com.github.picture2pc.android.ui.main.camerascreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
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
        CameraPreview(cameraViewModel = cameraViewModel)
        Column (
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            PictureButtons()
        }
        if (lastImage.value.byteCount == 4){ alpha = .0f }
        DisplayImage(image = lastImage.value, alpha)
    }
}