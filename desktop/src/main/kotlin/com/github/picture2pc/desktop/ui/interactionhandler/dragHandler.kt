package com.github.picture2pc.desktop.ui.interactionhandler

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.isInBounds
import org.jetbrains.skia.Point

class DragHandler(
    private val pP: PicturePreparation,
    private val rotationState: RotationState,
    private val cH: ClickHandler
) {
    private var dragStartPoint = Point(0f, 0f)
    var currentDragPoint: MutableState<Point> = mutableStateOf(Point(0f, 0f))
    var dragActive: MutableState<Boolean> = mutableStateOf(false)

    fun handleDrag(change: PointerInputChange, dragAmount: Offset) {
        if (change.id.value.toInt() != 0) return
        if (currentDragPoint.value == Point(0f, 0f)) currentDragPoint.value = dragStartPoint

        currentDragPoint.value = Point(
            currentDragPoint.value.x + dragAmount.x * pP.ratio,
            currentDragPoint.value.y + dragAmount.y * pP.ratio
        )

        dragActive.value = currentDragPoint.value.isInBounds(pP.editedBitmapBound)
        pP.setDisplayedZoomedBitmap(
            currentDragPoint.value
        )

        pP.updateEditedBitmap()
    }

    fun resetDrag() {
        cH.handleClick(
            Offset(
                currentDragPoint.value.x / pP.ratio,
                currentDragPoint.value.y / pP.ratio
            )
        )
        pP.reset(
            resetOverlay = false,
            resetClicks = false,
            resetEditedBitmap = false
        )
        dragStartPoint = Point(0f, 0f)
        currentDragPoint.value = Point(0f, 0f)
        dragActive.value = false
    }

    fun setDragStart(dragStart: Offset) {
        dragStartPoint = Point(
            dragStart.x * pP.ratio,
            dragStart.y * pP.ratio
        )
        pP.setDisplayedZoomedBitmap(dragStartPoint)
        dragActive.value = true
    }
}

