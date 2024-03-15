package com.github.picture2pc.android.ui.main.camerascreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.camerascreen.elements.TakePictureButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraController
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview

@Composable
fun CameraScreen(
    cameraController: CameraController,
    functionReturn: () -> Unit,
    functionSend: () -> Unit
) {
    Column(verticalArrangement = Arrangement.Top) {
        CameraPreview(cameraController, modifier = Modifier.fillMaxSize())
    }
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        TakePictureButton(
            onClickReturn = { functionReturn() },
            onClickPicture = { cameraController.takePicture() },
            onClickSend = { functionSend() }
        )
    }
}