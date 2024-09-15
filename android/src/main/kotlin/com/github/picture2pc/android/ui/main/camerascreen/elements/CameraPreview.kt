package com.github.picture2pc.android.ui.main.camerascreen.elements

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel

@Composable
fun CameraPreview(
    cameraViewModel: CameraViewModel
) {
    AndroidView(
        factory = { PreviewView(it).apply { cameraViewModel.setViewFinder(this) } }
    )
}