package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.desktop.extention.translate
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    pDVM: PictureDisplayViewModel = rememberKoinInject(),
) {
    val pictureBitmap = pDVM.currentPicture.value
    val clicks = pDVM.clickManager.clicks.collectAsState().value

    // Main Picture
    Image(
        bitmap = pictureBitmap.asComposeImageBitmap(),
        contentDescription = "Picture",
        modifier = Modifier
            .onSizeChanged { size -> pDVM.pP.calculateRatio(size) }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    pDVM.clickManager.addClick(
                        Offset(
                            offset.x - pDVM.pP
                                .displayPictureSize.width / 2, offset.y - pDVM.pP
                                .displayPictureSize.height / 2
                        )
                    )
                }
            }
    )

    Canvas(Modifier) {
        clicks.forEach {
            drawCircle(Colors.PRIMARY, 5f, it)
        }
    }

    /*// Clicked Points Overlay
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
                if (picDisVM.movementHandler.dragActive.value) PointerIcon(customCursor())
                else PointerIcon.Default
            )
    )*/

    // Zoom Overlay
    if (!pDVM.movementHandler.dragActive.value) return
    val offset = pDVM.calculateOffset(pDVM.rotationState.value)
    Box(
        Modifier
            .offset(offset.first.dp, offset.second.dp)
            .border(2.dp, Colors.PRIMARY, CircleShape)
    ) {
        val point = pDVM.movementHandler.currentDragPoint.value.translate(
            pDVM.rotationState.value,
            pDVM.pP.bounds
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