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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
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
import androidx.compose.ui.unit.toSize
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.desktop.extention.denormalize
import com.github.picture2pc.desktop.extention.normalize
import com.github.picture2pc.desktop.extention.toTopLeftOrigin
import com.github.picture2pc.desktop.extention.translate
import com.github.picture2pc.desktop.ui.constants.Settings
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
    val dragPoint = mHVM.dragPoint.collectAsState().value.translate(rotationState)
        .toTopLeftOrigin(pDVM.displayPictureSize)

    Box(
        modifier = Modifier.rotate(rotationState.angle)
            .onSizeChanged { size -> pDVM.calculateRatio(size.toSize()) },
    ) {
        Image(
            bitmap = pictureBitmap.asComposeImageBitmap(),
            contentDescription = "Picture",
            modifier = Modifier
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
            val scale = pDVM.displayPictureSize.minDimension

            // Part that is responsible for hover zoomed in preview
            if (isDragging)
                translate(
                    dragPoint.x,
                    dragPoint.y
                ) {
                    clipPath(Path().apply {
                        addOval(
                            Rect(
                                Offset(
                                    Settings.ZOOM_DIAMETER,
                                    Settings.ZOOM_DIAMETER
                                ) * -scale,
                                Size(
                                    Settings.ZOOM_DIAMETER * scale * 2,
                                    Settings.ZOOM_DIAMETER * scale * 2
                                )
                            )
                        )
                    }) {
                        translate(
                            -dragPoint.x * Settings.ZOOM_FACTOR,
                            -dragPoint.y * Settings.ZOOM_FACTOR
                        ) {
                            scale(Settings.ZOOM_FACTOR / pDVM.getRatio()) { // Scaled picture
                                drawImage(
                                    pictureBitmap.asComposeImageBitmap()
                                )
                            }
                        }
                    }
                    drawCircle(
                        Colors.PRIMARY,
                        Settings.ZOOM_DIAMETER * 0.1f * scale,
                        style = Stroke(width = 2f)
                    )
                    drawCircle(
                        Colors.PRIMARY,
                        Settings.ZOOM_DIAMETER * scale,
                        style = Stroke(width = 2f)
                    )
                }

            clicks.forEach {
                drawCircle(Colors.PRIMARY, 5f, it.denormalize(pDVM.displayPictureSize))
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
}