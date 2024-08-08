package com.github.picture2pc.desktop.ui.main.picturedisplay

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    pictureDisplayViewModel: PictureDisplayViewModel = rememberKoinInject(),
) {
    val pictureBitmap = pictureDisplayViewModel.currentPicture.collectAsState().value
    val overlayBitmap = pictureDisplayViewModel.overlayPicture.collectAsState().value

    val picture = pictureBitmap.asComposeImageBitmap()
    val overlay = overlayBitmap.asComposeImageBitmap()

    if (pictureBitmap.isEmpty) return
    Image(
        bitmap = picture,
        contentDescription = "Picture",
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                pictureDisplayViewModel.setPicture()
                pictureDisplayViewModel.picturePreparation.calculateRatio(
                    layoutCoordinates.size
                )
            }
    )
    if (overlayBitmap.isEmpty) return
    Image(
        bitmap = overlay,
        contentDescription = "Overlay",
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    pictureDisplayViewModel.picturePreparation.addClick(offset)
                }
            }
    )
}