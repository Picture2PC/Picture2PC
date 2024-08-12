package com.github.picture2pc.desktop.extention

import org.jetbrains.skia.Point
import kotlin.math.pow
import kotlin.math.sqrt

fun Point.distanceTo(other: Point): Float {
    return sqrt((other.x - this.x).pow(2) + (other.y - this.y).pow(2))
}