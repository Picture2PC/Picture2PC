package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.desktop.extention.minus
import com.github.picture2pc.desktop.ui.util.customCursor
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    pDVM: PictureDisplayViewModel = rememberKoinInject(),
) {
    val pictureBitmap = pDVM.currentPicture.value
    val clicks = pDVM.movementHandler.clicks.collectAsState().value
    val isDragging = pDVM.movementHandler.dragging.collectAsState().value
    val dragPoint = pDVM.movementHandler.dragPoint.collectAsState().value

    // Main Picture
    Image(
        bitmap = pictureBitmap.asComposeImageBitmap(),
        contentDescription = "Picture",
        modifier = Modifier
            .onSizeChanged { size -> pDVM.pP.calculateRatio(size.toSize()) }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    pDVM.movementHandler.addClick(
                        (offset - pDVM.pP.displayPictureSize),
                        pDVM.rotationState.value
                    )
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragStart ->
                        pDVM.movementHandler.setDrag(
                            dragStart - pDVM.pP.displayPictureSize,
                            pDVM.pP.displayPictureSize,
                            pDVM.rotationState.value,
                            true
                        )
                    },
                    onDrag = { change, _ ->
                        pDVM.movementHandler.setDrag(
                            change.position,
                            pDVM.pP.displayPictureSize,
                            pDVM.rotationState.value
                        )
                    },
                    onDragEnd = {
                        pDVM.movementHandler.endDrag(
                            pDVM.pP.displayPictureSize,
                            pDVM.rotationState.value
                        )
                    }
                )
            }
            .pointerHoverIcon(
                if (pDVM.movementHandler.dragActive.value) PointerIcon(customCursor())
                else PointerIcon.Default
            )
    )

    Canvas(Modifier) {
        clicks.forEach { drawCircle(Colors.PRIMARY, 5f, it) }
        if (clicks.size == 4) {
            drawPath(
                Path().apply {
                    moveTo(clicks[0].x, clicks[0].y)
                    lineTo(clicks[1].x, clicks[1].y)
                    lineTo(clicks[2].x, clicks[2].y)
                    lineTo(clicks[3].x, clicks[3].y)
                    close()
                },
                Colors.PRIMARY,
                style = Stroke(width = 2f)
            )
        }
    }

    // Zoom Overlay
    /*if (!isDragging) return TODO: Fix implementation of zoom overlay movement
    Box(
        Modifier
            .offset(dragPoint.x.dp, dragPoint.y.dp)
            .border(2.dp, Colors.PRIMARY, CircleShape)
    ) {
        val pictureSize = pDVM.pP.displayPictureSize
        Canvas(Modifier.size(Settings.ZOOM_DIAMETER.dp).align(Alignment.Center)) {
            clipPath(Path().apply { addOval(Rect(Offset.Zero, size)) }) {
                translate(
                    left = -(dragPoint.x * Settings.SCALE) - (pictureSize.width / 2),
                    top = -(dragPoint.y * Settings.SCALE) - (pictureSize.height / 2)
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
        Canvas(Modifier.size(10.dp).align(Alignment.Center)) {
            drawCircle(Colors.PRIMARY, style = Stroke(width = 2f))
        }
    }*/
}