package com.github.picture2pc.android.ui.main.camerascreen.elements

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun DisplayPicture(
    image: Bitmap, alpha: Float
){
    Image(
        bitmap = image.asImageBitmap(),
        contentDescription = "current image",
        modifier = Modifier
            .height(200.dp)
            .background(color = Color.Black.copy(alpha))
            .padding(5.dp)
    )
}