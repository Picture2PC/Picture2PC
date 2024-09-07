package com.github.picture2pc.desktop.ui.interactionhandler

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.github.picture2pc.common.ui.Icons.Desktop
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.distanceTo
import com.github.picture2pc.desktop.extention.isInBounds
import com.github.picture2pc.desktop.extention.translate
import com.github.picture2pc.desktop.ui.constants.Settings
import kotlin.math.atan2

enum class DraggingSpeed(val iconPath: String, val speed: Float) {
    SLOW(Desktop.SLOW, Settings.SLOW_DRAGGING_SPEED),
    FAST(Desktop.FAST, Settings.HIGH_DRAGGING_SPEED);

    fun next(): DraggingSpeed = when (this) {
        SLOW -> FAST
        FAST -> SLOW
    }
}

class MovementHandler(
    private val rotation: MutableState<RotationState>,
    private val pP: PicturePreparation,
) {
    private val clicks = pP.clicks
    private var dragStartPoint = Pair(0f, 0f)
    var currentDragPoint: MutableState<Pair<Float, Float>> = mutableStateOf(Pair(0f, 0f))
    var dragActive: MutableState<Boolean> = mutableStateOf(false)
    private var movedPoint = false

    val draggingSpeed = mutableStateOf(DraggingSpeed.FAST)

    fun handleClick(offset: Offset) {
        val point = Pair(offset.x * pP.ratio, offset.y * pP.ratio).translate(
            rotation.value,
            pP.bounds
        )
        if (!point.isInBounds(pP.bounds)) return

        if (clicks.size == 4) pP.reset(resetEditedBitmap = false)
        if (clicks.isNotEmpty()) pP.drawCircle(clicks.last(), filled = true)

        clicks.add(point)
        pP.drawCircle(point, clicks.size >= 4)

        if (clicks.size == 4) {
            sortClicksToRectangle()
            pP.drawPolygon(clicks)
        }
        pP.updateEditedBitmap()
    }

    private fun sortClicksToRectangle() {
        if (clicks.size != 4) return

        // Calculate the centroid of the four points
        val centroid = Pair(
            clicks.map { it.first }.average().toFloat(),
            clicks.map { it.second }.average().toFloat()
        )

        // Calculate the angle of each point relative to the centroid
        val angles = clicks.map { point ->
            val angle = atan2(
                (point.second - centroid.second).toDouble(),
                (point.first - centroid.first).toDouble()
            )
            Pair(point, angle)
        }

        // Sort points based on the angle
        val sortedPoints = angles.sortedBy { it.second }.map { it.first }

        // Update the clicks list with sorted points
        clicks.clear()
        clicks.addAll(sortedPoints)
    }

    private fun resetDrag() {
        pP.reset(
            resetOverlay = false,
            resetClicks = false,
            resetEditedBitmap = false
        )
        dragStartPoint = Pair(0f, 0f)
        currentDragPoint.value = Pair(0f, 0f)
        dragActive.value = false
    }

    fun handleDrag(change: PointerInputChange) {
        val ratio = pP.ratio
        val currentPosition = change.position

        if (change.id.value.toInt() != 0) return
        if (currentDragPoint.value == Pair(0f, 0f)) currentDragPoint.value = dragStartPoint

        currentDragPoint.value = Pair(currentPosition.x * ratio, currentPosition.y * ratio)
        dragActive.value = currentDragPoint.value.isInBounds(pP.bounds)
        pP.updateEditedBitmap()
    }

    fun setDragStart(dragStart: Offset) {
        dragStartPoint = Pair(dragStart.x * pP.ratio, dragStart.y * pP.ratio)

        if (pP.clicks.isNotEmpty()) {
            val distances = mutableListOf<Float>()
            for (click in pP.clicks) {
                val distance =
                    dragStartPoint.translate(rotation.value, pP.bounds).distanceTo(click)
                distances.add(distance)
            }
            val indexOfLowestDistance = distances.indexOf(distances.minOrNull())

            if (distances[indexOfLowestDistance] < 20 && pP.clicks.isNotEmpty()) {
                pP.clicks.removeAt(indexOfLowestDistance)
                movedPoint = true
            }
            dragActive.value = true
        }
    }

    fun endDrag() {
        if (pP.clicks.size == 4) {
            pP.clicks.clear()
        }
        val translatedPoint = currentDragPoint.value.translate(rotation.value, pP.bounds)
        if (translatedPoint.isInBounds(pP.bounds)) {
            pP.clicks.add(translatedPoint)
        }
        pP.redrawAllPoints()
        if (pP.clicks.size == 4) {
            sortClicksToRectangle()
            pP.drawPolygon(pP.clicks)
        }
        resetDrag()
    }
}