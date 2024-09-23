package com.github.picture2pc.desktop.ui.interactionhandler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import com.github.picture2pc.common.ui.Icons.Desktop
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.extention.distanceTo
import com.github.picture2pc.desktop.extention.isInBounds
import com.github.picture2pc.desktop.extention.toCenteredOrigin
import com.github.picture2pc.desktop.extention.toTopLeftOrigin
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
    // 0, 0 is the center of the picture
    private val _clicks = MutableStateFlow<List<Offset>>(emptyList())
    val clicks: StateFlow<List<Offset>> = _clicks
    private var dragStart: Offset = Offset(0f, 0f)
    private val _dragPoint = MutableStateFlow(Offset.Zero)
    val dragPoint: StateFlow<Offset> get() = _dragPoint
    val dragging = MutableStateFlow(false)
    val dragActive = MutableStateFlow(false)
    val draggingSpeed = MutableStateFlow(DraggingSpeed.FAST)

    fun setClicks(clicks: List<Offset>) {
        _clicks.value = clicks
    }

    fun addClick(
        click: Offset, rotationState: RotationState
    ) {
        if (clicks.value.size == 4) clear()
        _clicks.value += click.translate(rotationState)
        if (clicks.value.size == 4) setToSorted()
    }

    private fun removeClick(click: Offset) {
        _clicks.value -= click
    }

    fun clear() {
        _clicks.value = emptyList()
    }

    private fun setToSorted() {
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
        setClicks(angles.sortedBy { it.second }.map { it.first })
    }

    private fun getClosestPoint(
        point: Offset,
        rotationState: RotationState
    ): Pair<Offset, Float> {
        val distances = mutableListOf<Float>()
        for (click in clicks.value) {
            val distance = point.translate(rotationState).distanceTo(click)
            distances.add(distance)
        }
        val shortestDistance = distances.minOrNull() ?: 0f
        val indexOfLowestDistance = distances.indexOf(shortestDistance)
        return Pair(clicks.value[indexOfLowestDistance], shortestDistance)
    }

    //Dragging stuffs
    fun setDrag(
        dragPoint: Offset,
        pictureSize: Size,
        rotationState: RotationState,
        isStartingPoint: Boolean = false
    ) {
        if (clicks.value.isNotEmpty() && isStartingPoint) {
            val (closestPoint, distance) = getClosestPoint(dragPoint, rotationState)
            println(distance)
            if (distance < 10) {
                removeClick(closestPoint)
                dragStart = closestPoint.toCenteredOrigin(pictureSize)
            }
        }
        this._dragPoint.value = dragPoint.toCenteredOrigin(pictureSize)
        dragging.value = true
    }

    fun endDrag(
        pictureSize: Size, rotationState: RotationState,
    ) {
        if (!dragPoint.value.toTopLeftOrigin(pictureSize).isInBounds(pictureSize.toRect())
        ) return
        addClick(dragPoint.value, rotationState)
        dragging.value = false
    }
}