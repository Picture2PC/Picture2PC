package com.github.picture2pc.android.data.edgedetection

import android.content.Context
import android.graphics.Bitmap
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect

interface EdgeDetect {
    fun load(context: Context)
    fun detect(bit: Bitmap): List<DetectedBox>
}

data class DetectedBox(
    val box: Rect,
    val points: List<Point>,
    val masks: Mat,
) {
    // The box that contains the points
    val pointsBox: List<Point>
        get() {
            val tl = points.minByOrNull { it.x + it.y }
            val br = points.maxByOrNull { it.x + it.y }
            val tr = points.maxByOrNull { it.x - it.y }
            val bl = points.minByOrNull { it.x - it.y }
            return listOf(tl!!, tr!!, br!!, bl!!)
        }
}
