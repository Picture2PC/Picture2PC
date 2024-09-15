package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.desktop.extention.translate
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.ui.util.customCursor
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    picDisVM: PictureDisplayViewModel = rememberKoinInject(),
) {
    val pictureBitmap = picDisVM.currentPicture.value
    val overlayBitmap = picDisVM.overlayPicture.value
    val pP = picDisVM.pP
    // Main Picture
    Image(
        bitmap = pictureBitmap.asComposeImageBitmap(),
        contentDescription = "Picture",
        modifier = Modifier.onSizeChanged { size -> pP.calculateRatio(size) }
    )

    // Clicked Points Overlay
    Image(
        bitmap = overlayBitmap.asComposeImageBitmap(),
        contentDescription = "Overlay",
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragStart: Offset ->
                        picDisVM.movementHandler.setDragStart(dragStart)
                    },
                    onDrag = { change, _ ->
                        picDisVM.movementHandler.handleDrag(change)
                    },
                    onDragEnd = { picDisVM.movementHandler.endDrag() }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    picDisVM.movementHandler.handleClick(offset)
                }
            }
            .pointerHoverIcon(
                if (picDisVM.movementHandler.dragActive.value) PointerIcon(
                    customCursor()
                )
                else PointerIcon.Default
            )
    )

    // Zoom Overlay
    if (!picDisVM.movementHandler.dragActive.value) return
    val offset = picDisVM.calculateOffset(picDisVM.rotationState.value)
    Box(
        Modifier
            .offset(offset.first.dp, offset.second.dp)
            .border(2.dp, Colors.PRIMARY, CircleShape)
    ) {
        val point = picDisVM.movementHandler.currentDragPoint.value.translate(
            picDisVM.rotationState.value,
            picDisVM.pP.bounds
        )
        Canvas(Modifier.size(Settings.ZOOM_DIAMETER.dp).align(Alignment.Center)) {
            clipPath(Path().apply { addOval(Rect(Offset.Zero, size)) }) {
                translate(
                    left = -(point.first * Settings.SCALE) + 110,
                    top = -(point.second * Settings.SCALE) + 110
                ) {
                    scale(Settings.SCALE) {
                        drawImage(
                            image = pictureBitmap.asComposeImageBitmap(),
                            topLeft = Offset.Zero
                        )
                    }
                }
            }
        }
        Canvas(
            Modifier.size(10.dp).align(Alignment.Center)
        ) {
            drawCircle(Colors.PRIMARY, style = Stroke(width = 2f))
        }
    }
}

@Composable
fun PictureDisplay() {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.align(Alignment.TopStart).padding(10.dp)) { RotationButtons() }
        Box(Modifier.align(Alignment.Center).padding(10.dp)) { Picture() }
        Box(Modifier.align(Alignment.TopEnd).padding(10.dp)) { ZoomSpeedButton() }
    }
}