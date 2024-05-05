package com.github.picture2pc.android.ui.main.bigpicturescreen.elements

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import com.github.picture2pc.android.extentions.rotate
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun BigPicture(
    rotation: Float = 0f,
    cameraViewModel: CameraViewModel = rememberKoinInject(),
) {
    Image(
        bitmap = cameraViewModel.getLastImage().rotate(rotation).asImageBitmap(),
        contentDescription = "Big last picture"
    )
}