package com.github.picture2pc.desktop.ui.main.picturedisplay

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    pictureDisplayViewModel: PictureDisplayViewModel = rememberKoinInject()
) {
    val picture = pictureDisplayViewModel.currentPicture.collectAsState().value
    picture?.let {
        println("${it.height}, ${it.width}")
        Image(
            bitmap = it.toComposeImageBitmap(),
            contentDescription = "Picture",
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures { offset ->
                    println("Clicked at: x=${offset.x}, y=${offset.y}")}
            }
        )
    }
}