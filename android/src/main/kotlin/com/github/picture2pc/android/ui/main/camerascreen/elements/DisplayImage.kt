package com.github.picture2pc.android.ui.main.camerascreen.elements

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun DisplayImage(lastImage: Bitmap){
    val cameraViewModel: CameraViewModel = rememberKoinInject()
    Image(
        painter = BitmapPainter(lastImage.asImageBitmap()),
        contentDescription = "current image",
        modifier = Modifier.wrapContentSize().rotate(90f).height(200.dp)
    )
}