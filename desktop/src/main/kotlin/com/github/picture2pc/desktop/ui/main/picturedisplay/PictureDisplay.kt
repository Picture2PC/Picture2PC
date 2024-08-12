package com.github.picture2pc.desktop.ui.main.picturedisplay

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    picDisVM: PictureDisplayViewModel = rememberKoinInject(),
) {
    val pictureBitmap = picDisVM.currentPicture.value
    val overlayBitmap = picDisVM.overlayPicture.value
    val dragOverlayBitmap = picDisVM.dragOverlayPicture.value

    Image(
        bitmap = pictureBitmap.asComposeImageBitmap(),
        contentDescription = "Picture",
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                picDisVM.picturePreparation.calculateRatio(
                    layoutCoordinates.size
                )
            }
    )

    Image(
        bitmap = dragOverlayBitmap.asComposeImageBitmap(),
        contentDescription = "DragOverlay",
    )

    Image(
        bitmap = overlayBitmap.asComposeImageBitmap(),
        contentDescription = "Overlay",
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragStart: Offset -> picDisVM.picturePreparation.setDragStart(dragStart)
                    },
                    onDrag = {
                             change, dragAmount -> picDisVM.picturePreparation
                                 .handleDrag(change, dragAmount)
                    },
                    onDragEnd = { picDisVM.picturePreparation.resetDrag() }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    picDisVM.picturePreparation.addClick(offset)
                }
            }
    )
}