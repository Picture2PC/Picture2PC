package com.github.picture2pc.android.ui.main.camerascreen.elements

import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.viewinterop.AndroidView
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.common.ui.Shapes

@Composable
fun CameraPreview(
    cameraViewModel: CameraViewModel
) {
    val pictureCorners = cameraViewModel.pictureCorners.collectAsState(null)
    Box(
        modifier = Modifier
            .clip(Shapes.MOBILE) // Clip the content to a specific shape
            .fillMaxSize() // Fill the available size
    ) {
        val av = AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PreviewView(it).apply {
                    cameraViewModel.setViewFinder(this)
                }
            }
        )

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val detectedBox = pictureCorners.value ?: return@Canvas
            val ratioX = size.width / detectedBox.masks.width().toFloat()
            val ratioY = size.height / detectedBox.masks.height().toFloat()
            detectedBox.pointsBox.onEach {
                drawCircle(
                    color = Color.Green,
                    radius = 10f,
                    center = Offset(it.x.toFloat() * ratioX, it.y.toFloat() * ratioY),
                    style = Fill
                )
            }
        }

    }
}