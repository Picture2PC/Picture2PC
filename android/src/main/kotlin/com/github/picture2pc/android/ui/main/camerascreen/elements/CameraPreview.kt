package com.github.picture2pc.android.ui.main.camerascreen.elements

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.common.ui.Shapes

@Composable
fun CameraPreview(
    cameraViewModel: CameraViewModel
) {
    AndroidView(
        modifier = Modifier.clip(Shapes.MOBILE),
        factory = {
            PreviewView(it).apply {
                cameraViewModel.setViewFinder(this)
            }
        }
    )
}