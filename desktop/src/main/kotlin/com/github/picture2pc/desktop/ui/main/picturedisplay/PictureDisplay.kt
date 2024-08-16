package com.github.picture2pc.desktop.ui.main.picturedisplay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    picDisVM: PictureDisplayViewModel = rememberKoinInject(),
) {
    val pictureBitmap = picDisVM.currentPicture.value
    val overlayBitmap = picDisVM.overlayPicture.value
    val dragOverlayBitmap = picDisVM.dragOverlayPicture.value
    val zoomedBitmap = picDisVM.zoomedBitmap.value

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
        contentDescription = "Drag Overlay",
    )

    Image(
        bitmap = overlayBitmap.asComposeImageBitmap(),
        contentDescription = "Overlay",
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragStart: Offset ->
                        picDisVM.picturePreparation.setDragStart(dragStart)
                    },
                    onDrag = { change, dragAmount ->
                        picDisVM.picturePreparation.handleDrag(change, dragAmount)
                    },
                    onDragEnd = { picDisVM.picturePreparation.resetDrag() }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    picDisVM.picturePreparation.handleClick(offset)
                }
            }
    )

    if (!picDisVM.picturePreparation.dragActive.value) return
    Box {
        Image(
            bitmap = zoomedBitmap.asComposeImageBitmap(),
            contentDescription = "Zoomed Point",
            modifier = Modifier
                .offset(
                    picDisVM.picturePreparation.calculateOffset().first,
                    picDisVM.picturePreparation.calculateOffset().second
                )
                .clip(CircleShape)
                .border(Borders.BORDER_THICK, Colors.PRIMARY, CircleShape)
        )
        Canvas(Modifier.size(10.dp).align(Alignment.Center).offset(
                picDisVM.picturePreparation.calculateOffset().first,
            picDisVM.picturePreparation.calculateOffset().second
        )
        ){
            drawCircle(Colors.PRIMARY, style = Stroke(width = 2f))
        }
    }
}