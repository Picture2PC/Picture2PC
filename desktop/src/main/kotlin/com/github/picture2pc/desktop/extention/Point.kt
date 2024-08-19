package com.github.picture2pc.desktop.extention

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import org.jetbrains.skia.Point
import kotlin.math.pow
import kotlin.math.sqrt

fun Point.distanceTo(secondPoint: Point): Float {
    return sqrt((secondPoint.x - this.x).pow(2) + (secondPoint.y - this.y).pow(2))
}

fun Point.isInBounds(rectangle: Rect): Boolean {
    return !(
        this.x < rectangle.left  ||
        this.x > rectangle.right ||
        this.y < rectangle.top   ||
        this.y > rectangle.bottom
    )
}

fun Point.toOffset(): Offset {
    return Offset(this.x, this.y)
}

fun Point.toOpenCVPoint(): org.opencv.core.Point {
    return org.opencv.core.Point(this.x.toDouble(), this.y.toDouble())
}