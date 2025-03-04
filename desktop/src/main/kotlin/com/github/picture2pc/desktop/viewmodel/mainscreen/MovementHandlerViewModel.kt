package com.github.picture2pc.desktop.viewmodel.mainscreen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.github.picture2pc.common.ui.Icons.Desktop
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.data.next
import com.github.picture2pc.desktop.extention.clampInBounds
import com.github.picture2pc.desktop.extention.distanceTo
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
    private val _draggingSpeed = MutableStateFlow(DraggingSpeed.SLOW)
    val draggingSpeed = _draggingSpeed.asStateFlow()
    private val _clicks: MutableStateFlow<List<Offset>> = MutableStateFlow(listOf())
    val clicks: StateFlow<List<Offset>> = _clicks.asStateFlow()
    val rotationState = MutableStateFlow(RotationState.ROTATION_0)
    private val _dragPoint = MutableStateFlow<Offset?>(null)
    val dragPoint = _dragPoint.asStateFlow()

    var prevEnabled: Boolean = true
        private set

    /**
     * @param click A normalized offset starting at (0, 0)[top left] ending (1, 1)[bottom right]
     */
    fun addClick(click: Offset) {
        val clickC = clampOffset(click)
        if (clicks.value.size == CLICK_COUNT) clearClicks()
        _clicks.value = sortClicks(clicks.value + clickC) // Set clicks to sort clicks
    }

    fun setDrag(pos: Offset) {
        val posC = clampOffset(pos)
        if (clicks.value.isNotEmpty() && dragPoint.value == null) {
            val (closestPoint, distance) = getClosestPoint(posC)
            if (distance < BUTTON_CHOOSE_HITRADIUS)
                removeClick(closestPoint)
        }
        _dragPoint.value = posC
    }

    fun endDrag() {
        dragPoint.value?.let { addClick(it) }
        _dragPoint.value = null
    }

    private fun clampOffset(offset: Offset): Offset {
        val min = (0.5 * BUTTON_CHOOSE_HITRADIUS).toFloat()
        val max = (1f - 0.5 * BUTTON_CHOOSE_HITRADIUS).toFloat()
        return offset.clampInBounds(Rect(min, min, max, max))
    }

    private fun removeClick(click: Offset) {
        _clicks.value -= click
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

    fun sortClicks(clicks: List<Offset>): List<Offset> {
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

    private fun clearClicks() {
        _clicks.value = listOf()
    }

    fun clear() {
        endDrag()
        clearClicks()
    }

    fun setClicks(clicks: List<Offset>) {
        _clicks.value = clicks.map {
            clampOffset(it)
        }
    }

    fun rotate(clockwise: Boolean) {
        rotationState.value = rotationState.value.next(clockwise)
        _clicks.value = _clicks.value.map {
            it.toCenteredOrigin(Size(1f, 1f)).translate(clockwise).toTopLeftOrigin(Size(1f, 1f))
        }
    }

    fun updateDraggingSpeed() {
        _draggingSpeed.value = draggingSpeed.value.next()
    }

    companion object {
        const val CLICK_COUNT = 4
        const val BUTTON_CHOOSE_HITRADIUS = 0.01
    }
}