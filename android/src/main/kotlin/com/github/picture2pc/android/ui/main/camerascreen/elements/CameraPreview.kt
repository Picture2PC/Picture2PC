package com.github.picture2pc.android.ui.main.camerascreen.elements

import androidx.camera.view.PreviewView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraViewModel: CameraViewModel
) {
    AndroidView(
        modifier = modifier.clip(RoundedCornerShape(20.dp)),
        factory = {
            PreviewView(it).apply {
                cameraViewModel.setViewFinder(this)
            }
        }
    )
}