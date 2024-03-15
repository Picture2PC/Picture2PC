package com.github.picture2pc.android.ui.main.camerascreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview
import com.github.picture2pc.android.ui.main.camerascreen.elements.TakePictureButton

@Composable
fun CameraScreen(
    functionReturn: () -> Unit,
    functionSend: () -> Unit
) {
    val context = LocalContext.current
    val cameraController = CameraPreview(modifier = Modifier.fillMaxSize())
    val takePicture = { cameraController.takePicture(context) }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        TakePictureButton(
            onClickReturn = { functionReturn() },
            onClickPicture = takePicture,
            onClickSend = { functionSend() }
        )
    }
}