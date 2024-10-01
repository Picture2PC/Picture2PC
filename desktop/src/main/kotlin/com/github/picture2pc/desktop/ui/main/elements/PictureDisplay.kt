package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.desktop.extention.denormalize
import com.github.picture2pc.desktop.extention.normalize
import com.github.picture2pc.desktop.ui.util.customCursor
import com.github.picture2pc.desktop.viewmodel.mainscreen.MovementHandlerViewModel
import com.github.picture2pc.desktop.viewmodel.mainscreen.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    pDVM: PictureDisplayViewModel = rememberKoinInject(),
    mHVM: MovementHandlerViewModel = rememberKoinInject()
) {
    val pictureBitmap = pDVM.currentPicture.value
    val clicks = mHVM.clicks.collectAsState().value
    val rotationState = mHVM.rotationState.collectAsState().value
    val isDragging = mHVM.dragging.collectAsState().value
    val dragPoint = mHVM.dragPoint.collectAsState().value

    Box(
        modifier = Modifier.rotate(rotationState.angle)
    ) {
        Image(
            bitmap = pictureBitmap.asComposeImageBitmap(),
            contentDescription = "Picture",
            modifier = Modifier
                .onSizeChanged { size -> pDVM.calculateRatio(size.toSize()) }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        mHVM.addClick(offset.normalize(pDVM.displayPictureSize))
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { dragStart ->
                            mHVM.setDrag(
                                dragStart,
                                pDVM.displayPictureSize,
                                true
                            )
                        },
                        onDrag = { change, _ ->
                            mHVM.setDrag(
                                change.position,
                                pDVM.displayPictureSize
                            )
                        },
                        onDragEnd = {
                            mHVM.endDrag(pDVM.displayPictureSize)
                        }
                    )
                }
                .pointerHoverIcon(
                    if (mHVM.dragActive.value) PointerIcon(customCursor())
                    else PointerIcon.Default
                )
        )

        Canvas(Modifier) {
            clicks.forEach {
                drawCircle(
                    Colors.PRIMARY,
                    5f,
                    it.denormalize(pDVM.displayPictureSize)
                )
            }
            if (clicks.size == 4) {
                val tl = clicks[0].denormalize(pDVM.displayPictureSize)
                val tr = clicks[1].denormalize(pDVM.displayPictureSize)
                val br = clicks[2].denormalize(pDVM.displayPictureSize)
                val bl = clicks[3].denormalize(pDVM.displayPictureSize)
                drawPath(
                    Path().apply {
                        moveTo(tl.x, tl.y)
                        lineTo(tr.x, tr.y)
                        lineTo(br.x, br.y)
                        lineTo(bl.x, bl.y)
                        close()
                    },
                    Colors.PRIMARY,
                    style = Stroke(width = 2f)
                )
            }
        }
    }

    /*if (!isDragging) return //TODO: Fix implementation of zoom overlay movement
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