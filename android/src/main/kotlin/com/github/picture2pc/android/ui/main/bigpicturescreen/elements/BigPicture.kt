package com.github.picture2pc.android.ui.main.bigpicturescreen.elements

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.github.picture2pc.android.data.takeimage.ImageManager
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun BigPicture(cameraViewModel: CameraViewModel = rememberKoinInject()){
    Image(
        bitmap = cameraViewModel.takenImages.replayCache.last().asImageBitmap(),
        contentDescription = "Big last picture"
    )
}