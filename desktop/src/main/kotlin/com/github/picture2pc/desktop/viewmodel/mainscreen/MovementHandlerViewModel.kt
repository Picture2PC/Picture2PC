package com.github.picture2pc.desktop.viewmodel.mainscreen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import com.github.picture2pc.common.ui.Icons.Desktop
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.extention.distanceTo
import com.github.picture2pc.desktop.extention.isInBounds
import com.github.picture2pc.desktop.extention.normalize
import com.github.picture2pc.desktop.extention.toCenteredOrigin
import com.github.picture2pc.desktop.extention.toTopLeftOrigin
import com.github.picture2pc.desktop.extention.translate
import com.github.picture2pc.desktop.ui.constants.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2

enum class DraggingSpeed(val iconPath: String, val speed: Float) {
    SLOW(Desktop.SLOW, Settings.SLOW_DRAGGING_SPEED),
    FAST(Desktop.FAST, Settings.HIGH_DRAGGING_SPEED);

    fun next(): DraggingSpeed = when (this) {
        SLOW -> FAST
        FAST -> SLOW
    }
}

class MovementHandlerViewModel {
    private val _clicks = MutableStateFlow<List<Offset>>(emptyList())

    /**
     * A list of **normalized** and **top left centered** clicks
     */
    val clicks: StateFlow<List<Offset>> = _clicks.asStateFlow()

    private var dragStart: Offset = Offset(0f, 0f)
    private val _dragPoint = MutableStateFlow(Offset.Zero)
    val dragPoint: StateFlow<Offset> get() = _dragPoint
    val dragging = MutableStateFlow(false)
    val dragActive = MutableStateFlow(false)
    val draggingSpeed = MutableStateFlow(DraggingSpeed.FAST)

    val rotationState = MutableStateFlow(RotationState.ROTATION_0)

    /**
     * Sets the clicks to the given list of clicks
     * The list should contain 4 clicks
     * @param clicks The list of clicks to set. The clicks should be **normalized**
     */
    fun setClicks(clicks: List<Offset>) {
        if (clicks.size != 4) return
        _clicks.value = sortClicks(clicks)
    }

    /**
     * Adds a click to the list of clicks
     * If the list has 4 clicks, it will sort them and set them
     * @param click The click to add. The click should be **normalized**
     */
    fun addClick(click: Offset) {
        if (clicks.value.size == 4) clear()
        _clicks.value += click.translate(rotationState.value)
        if (clicks.value.size == 4) setClicks(clicks.value) // Set clicks to sort clicks
    }

    /**
     * Removes a click from the list of clicks
     * @param click The click to remove. The click should be **normalized**
     */
    private fun removeClick(click: Offset) {
        _clicks.value -= click
    }

    /**
     * Clears the list of clicks
     */
    fun clear() {
        _clicks.value = emptyList()
    }

    /**
     * Sorts the clicks in a clockwise order
     * @param clicks The list of clicks to sort
     */
    private fun sortClicks(clicks: List<Offset>): List<Offset> {
        // Calculate the centroid of the four points
        val centroid = Offset(
            clicks.map { it.x }.average().toFloat(),
            clicks.map { it.y }.average().toFloat()
        )

        // Calculate the angle of each point relative to the centroid
        val angles = clicks.map { point ->
            val angle = atan2(
                (point.y - centroid.y).toDouble(),
                (point.x - centroid.x).toDouble()
            )
            Pair(point, angle)
        }
        return angles.sortedBy { it.second }.map { it.first }
    }

    /**
     * Returns the closest point to the given point
     * @param point The point to compare to
     */
    private fun getClosestPoint(point: Offset): Pair<Offset, Float> {
        val distances = mutableListOf<Float>()
        for (click in clicks.value) {
            val distance = point.translate(rotationState.value).distanceTo(click)
            distances.add(distance)
        }
        val shortestDistance = distances.minOrNull() ?: 0f
        val indexOfLowestDistance = distances.indexOf(shortestDistance)
        return Pair(clicks.value[indexOfLowestDistance], shortestDistance)
    }

    /**
     * Sets the drag point to the given point
     * @param dragPoint The point to set the drag point to. The point should **not be normalized**
     * @param pictureSize The size of the *displayed* picture
     * @param isStartingPoint Whether the drag point is the starting point of the drag
     */
    fun setDrag(
        dragPoint: Offset,
        pictureSize: Size,
        isStartingPoint: Boolean = false
    ) {
        if (clicks.value.isNotEmpty() && isStartingPoint) {
            val (closestPoint, distance) = getClosestPoint(dragPoint.normalize(pictureSize))
            if (distance < 0.01) {
                removeClick(closestPoint)
                dragStart = closestPoint.toCenteredOrigin(pictureSize)
            }
        }
        this._dragPoint.value = dragPoint.toCenteredOrigin(pictureSize)
        dragging.value = true
    }

    /**
     * Ends the drag
     * @param pictureSize The size of the *displayed* picture
     */
    fun endDrag(pictureSize: Size) {
        if (!dragPoint.value.toTopLeftOrigin(pictureSize).isInBounds(pictureSize.toRect())
        ) return
        addClick(dragPoint.value.toTopLeftOrigin(pictureSize).normalize(pictureSize))
        println(dragPoint.value.toTopLeftOrigin(pictureSize).normalize(pictureSize))
        dragging.value = false
    }
}