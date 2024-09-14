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
    val masks: Mat
)
