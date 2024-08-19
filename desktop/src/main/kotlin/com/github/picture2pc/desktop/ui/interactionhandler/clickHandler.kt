package com.github.picture2pc.desktop.ui.interactionhandler

import androidx.compose.ui.geometry.Offset
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.data.imageprep.constants.Paints
import com.github.picture2pc.desktop.extention.isInBounds
import org.jetbrains.skia.Point
import kotlin.math.atan2

class ClickHandler(
    private val pP: PicturePreparation,
) {
    private val clicks = pP.clicks

    fun handleClick(offset: Offset) {
        val point = Point(offset.x * pP.ratio, offset.y * pP.ratio)
        if (!point.isInBounds(pP.editedBitmapBound)) return

        if (clicks.size == 4) pP.reset(
            resetEditedBitmap = false,
            resetDragOverlay = false
        )
        if (clicks.isNotEmpty()) pP.drawCircle(clicks.last(), filled = true)

        clicks.add(point)
        pP.drawCircle(point, clicks.size >= 4)

        if (clicks.size == 4) {
            sortClicksToRectangle()
            pP.drawPolygon(clicks, Paints.stroke)
        }
        pP.updateEditedBitmap()
    }

    private fun sortClicksToRectangle() {
        if (clicks.size != 4) return

        // Calculate the centroid of the four points
        val centroid = Point(
            clicks.map { it.x }.average().toFloat(),
            clicks.map { it.y }.average().toFloat()
        )

        // Sort points based on the angle with the centroid
        clicks.sortBy { point ->
            atan2((point.y - centroid.y).toDouble(), (point.x - centroid.x).toDouble())
        }
    }
}