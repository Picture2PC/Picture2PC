package com.github.picture2pc.desktop.ui.main.elements

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
import androidx.compose.ui.layout.onSizeChanged
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
    val zoomedBitmap = picDisVM.zoomedBitmap.value

    Image(
        bitmap = pictureBitmap.asComposeImageBitmap(),
        contentDescription = "Picture",
        modifier = Modifier
            .onSizeChanged { size ->
                picDisVM.pP.calculateRatio(
                    size
                )
            }
    )

    Image(
        bitmap = overlayBitmap.asComposeImageBitmap(),
        contentDescription = "Overlay",
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragStart: Offset ->
                        picDisVM.dragHandler.setDragStart(dragStart)
                    },
                    onDrag = { change, dragAmount ->
                        picDisVM.dragHandler.handleDrag(change, dragAmount)
                    },
                    onDragEnd = { picDisVM.dragHandler.resetDrag() }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    picDisVM.clickHandler.handleClick(offset)
                }
            }
    )

    if (!picDisVM.dragHandler.dragActive.value) return
    Box(Modifier.offset((-5).dp, (-5).dp)) {
        Image(
            bitmap = zoomedBitmap.asComposeImageBitmap(),
            contentDescription = "Zoomed Point",
            modifier = Modifier
                .offset(
                    picDisVM.pP.calculateOffset().first,
                    picDisVM.pP.calculateOffset().second
                )
                .clip(CircleShape)
                .border(Borders.BORDER_THICK, Colors.SECONDARY, CircleShape)
        )

        Canvas(
            Modifier.size(10.dp).align(Alignment.Center).offset(
                picDisVM.pP.calculateOffset().first,
                picDisVM.pP.calculateOffset().second
            )
        ) {
            drawCircle(Colors.PRIMARY, style = Stroke(width = 2f))
        }
    }
}