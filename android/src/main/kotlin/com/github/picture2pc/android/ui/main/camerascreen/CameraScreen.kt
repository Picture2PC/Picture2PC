package com.github.picture2pc.android.ui.main.camerascreen

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.camerascreen.elements.BigPicture
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview
import com.github.picture2pc.android.ui.main.camerascreen.elements.DisplayPicture
import com.github.picture2pc.android.ui.main.camerascreen.elements.PictureButtons
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel = rememberKoinInject()
){
    var alpha = .5f
    val image = cameraViewModel.takenImages.collectAsState(initial = cameraViewModel.getBlankImage()).value
    val matrix = Matrix()
    matrix.postRotate(90f)
    val imageBitmap = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
    var showBigPicture by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        if (showBigPicture){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showBigPicture = !showBigPicture }
                    .align(Alignment.Center)
            ) {
                BigPicture(image = imageBitmap)
            }
        }
        else{
            Box(modifier = Modifier
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max)
                .align(Alignment.Center)
            ) {
                CameraPreview(cameraViewModel = cameraViewModel)
            }
            if (image.byteCount == 4){ alpha = .0f }
            Box(modifier = Modifier
                .align(Alignment.TopStart)
                .clickable { showBigPicture = !showBigPicture }
            ) {
                DisplayPicture(image = imageBitmap, alpha)
            }
            Box (modifier = Modifier
                .align(Alignment.BottomCenter)
            ) {
                PictureButtons()
            }
        }
    }
}

