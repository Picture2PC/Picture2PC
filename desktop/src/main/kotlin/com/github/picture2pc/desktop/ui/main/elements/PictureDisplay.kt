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
import androidx.compose.runtime.collectAsState
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
import com.github.picture2pc.desktop.extention.minus
import com.github.picture2pc.desktop.extention.translate
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.ui.util.customCursor
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    pDVM: PictureDisplayViewModel = rememberKoinInject(),
) {
    val pictureBitmap = pDVM.currentPicture.value
    val clicks = pDVM.movementHandler.clicks.collectAsState().value

    // Main Picture
    Image(
        bitmap = pictureBitmap.asComposeImageBitmap(),
        contentDescription = "Picture",
        modifier = Modifier
            .onSizeChanged { size -> pDVM.pP.calculateRatio(size) }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    pDVM.movementHandler.addClick(
                        offset - pDVM.pP.displayPictureSize
                    )
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragStart: Offset ->
                        pDVM.movementHandler.setDrag(
                            dragStart - pDVM.pP.displayPictureSize,
                            true
                        )
                    },
                    onDrag = { change, _ ->
                        pDVM.movementHandler.setDrag(change.position)
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
    if (!pDVM.movementHandler.dragging.value) return
    val offset = pDVM.calculateOffset(pDVM.rotationState.value)
    Box(
        Modifier
            .offset(offset.x.dp, offset.y.dp)
            .border(2.dp, Colors.PRIMARY, CircleShape)
    ) {
        val point = pDVM.movementHandler.dragPoint.translate(
            pDVM.rotationState.value,
            pDVM.pP.bounds
        )
        Canvas(Modifier.size(Settings.ZOOM_DIAMETER.dp).align(Alignment.Center)) {
            clipPath(Path().apply { addOval(Rect(Offset.Zero, size)) }) {
                translate(
                    left = -(point.x * Settings.SCALE) + 110,
                    top = -(point.y * Settings.SCALE) + 110
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
    }
}