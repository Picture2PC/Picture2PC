package com.github.picture2pc.desktop.ui.interactionhandler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.unit.toRect
import com.github.picture2pc.common.ui.Icons.Desktop
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.extention.distanceTo
import com.github.picture2pc.desktop.extention.isInBounds
import com.github.picture2pc.desktop.extention.minus
import com.github.picture2pc.desktop.extention.translate
import com.github.picture2pc.desktop.ui.constants.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.atan2

enum class DraggingSpeed(val iconPath: String, val speed: Float) {
    SLOW(Desktop.SLOW, Settings.SLOW_DRAGGING_SPEED),
    FAST(Desktop.FAST, Settings.HIGH_DRAGGING_SPEED);

    fun next(): DraggingSpeed = when (this) {
        SLOW -> FAST
        FAST -> SLOW
    }
}

class MovementHandler {
    private val _clicks = MutableStateFlow<List<Offset>>(emptyList())
    val clicks: StateFlow<List<Offset>> = _clicks
    private var dragStart: Offset = Offset(0f, 0f)
    var dragPoint: Offset = Offset(0f, 0f)
    val dragging = MutableStateFlow(false)
    val dragActive = MutableStateFlow(false)
    val draggingSpeed = MutableStateFlow(DraggingSpeed.FAST)

    private fun setClick(clicks: List<Offset>) {
        _clicks.value = clicks
    }

    fun addClick(click: Offset) {
        if (clicks.value.size == 4) clear()
        _clicks.value += click
        if (clicks.value.size == 4) setToSorted()
    }

    private fun removeClick(click: Offset) {
        _clicks.value -= click
    }

    fun clear() {
        _clicks.value = emptyList()
    }

    fun setToSorted() {
        // Calculate the centroid of the four points
        val centroid = Offset(
            clicks.value.map { it.x }.average().toFloat(),
            clicks.value.map { it.y }.average().toFloat()
        )

        // Calculate the angle of each point relative to the centroid
        val angles = clicks.value.map { point ->
            val angle = atan2(
                (point.y - centroid.y).toDouble(),
                (point.x - centroid.x).toDouble()
            )
            Pair(point, angle)
        }
        setClick(angles.sortedBy { it.second }.map { it.first })
    }

    private fun getClosestPoint(point: Offset): Pair<Offset, Float> {
        val distances = mutableListOf<Float>()
        for (click in clicks.value) {
            val distance = point.distanceTo(click)
            distances.add(distance)
        }
        val shortestDistance = distances.minOrNull() ?: 0f
        val indexOfLowestDistance = distances.indexOf(shortestDistance)
        return Pair(clicks.value[indexOfLowestDistance], shortestDistance)
    }

    //Dragging stuffs
    fun setDrag(dragPoint: Offset, startingPoint: Boolean = false) {
        val (closestPoint, distance) = getClosestPoint(dragPoint)
        if (startingPoint &&
            clicks.value.isNotEmpty() &&
            distance < 20
        ) dragStart = closestPoint
        this.dragPoint = dragPoint
    }

    fun endDrag(pictureSize: IntSize, rotationState: RotationState) {
        if (clicks.value.isEmpty()) return
        if (!dragPoint.isInBounds(pictureSize.toIntRect())) return
        removeClick(dragStart)
        addClick(
            dragPoint.minus(pictureSize).translate(
                rotationState, pictureSize.toIntRect().toRect()
            )
        )
    }
}