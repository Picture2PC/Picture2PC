package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.toSize
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.desktop.extention.denormalize
import com.github.picture2pc.desktop.extention.normalize
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.viewmodel.mainscreen.MovementHandlerViewModel
import com.github.picture2pc.desktop.viewmodel.mainscreen.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun Picture(
    pDVM: PictureDisplayViewModel = rememberKoinInject(),
    mHVM: MovementHandlerViewModel = rememberKoinInject()
) {
    val imageSize = remember { mutableStateOf(Size(1f, 1f)) }
    val canvasSize = remember { mutableStateOf(Size(1f, 1f)) }

    val pictureBitmap = pDVM.currentPicture.value
    val clicks = mHVM.clicks.collectAsState().value
    val dragPoint = mHVM.dragPoint.collectAsState().value

    Box(Modifier.onGloballyPositioned { canvasSize.value = it.size.toSize() }) {
        Image(
            bitmap = pictureBitmap.asComposeImageBitmap(),
            contentDescription = "Picture",
            modifier = Modifier
                .onGloballyPositioned {
                    imageSize.value = it.size.toSize(); pDVM.calculateRatio(imageSize.value)
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset -> mHVM.addClick(offset.normalize(imageSize.value)) }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { dragStart ->
                            mHVM.setDrag(dragStart.normalize(imageSize.value))
                        },
                        onDrag = { change, _ ->
                            mHVM.setDrag(change.position.normalize(imageSize.value))
                        },
                        onDragEnd = { mHVM.endDrag() }
                    )
                }
                .pointerHoverIcon(PointerIcon.Default)
        )
        Canvas(Modifier) {
            val scale = canvasSize.value.minDimension

            // Draw box around corners
            val selectedClicks =
                if (clicks.size == 3 && dragPoint != null && mHVM.prevEnabled) mHVM.sortClicks(
                    clicks + dragPoint
                ) else clicks
            if (selectedClicks.size == 4) {
                // Final Rectangle
                val tl = selectedClicks[0].denormalize(canvasSize.value)
                val tr = selectedClicks[1].denormalize(canvasSize.value)
                val br = selectedClicks[2].denormalize(canvasSize.value)
                val bl = selectedClicks[3].denormalize(canvasSize.value)
                val pathEffect = if (dragPoint != null) PathEffect.dashPathEffect(
                    floatArrayOf(10f, 10f),
                    0f
                ) else null
                drawPath(
                    Path().apply {
                        moveTo(tl.x, tl.y)
                        lineTo(tr.x, tr.y)
                        lineTo(br.x, br.y)
                        lineTo(bl.x, bl.y)
                        close()
                    },
                    Colors.PRIMARY,
                    style = Stroke(width = 2f, pathEffect = pathEffect)
                )

            }

            // Part that is responsible for hover zoomed in preview
            if (dragPoint != null) {
                val absoluteDragPoint = dragPoint.denormalize(canvasSize.value)
                translate(
                    absoluteDragPoint.x,
                    absoluteDragPoint.y
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
                            -absoluteDragPoint.x * Settings.ZOOM_FACTOR,
                            -absoluteDragPoint.y * Settings.ZOOM_FACTOR
                        ) {
                            scale(Settings.ZOOM_FACTOR / pDVM.getRatio()) { // Scaled picture
                                drawImage(pictureBitmap.asComposeImageBitmap())
                            }
                        }
                    }
                    drawCircle( //inner circle
                        Colors.PRIMARY,
                        Settings.ZOOM_DIAMETER * 0.1f * scale,
                        style = Stroke(width = 2f)
                    )
                    drawCircle( //outer circle
                        Colors.PRIMARY,
                        Settings.ZOOM_DIAMETER * scale,
                        style = Stroke(width = 2f)
                    )
                }
            }
            // Draw 4 corner dots
            clicks.forEach {
                drawCircle(Colors.PRIMARY, 0.01f * scale, it.denormalize(canvasSize.value))
            }
        }
    }
}
