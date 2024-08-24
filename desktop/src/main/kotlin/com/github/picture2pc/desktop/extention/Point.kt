package com.github.picture2pc.desktop.extention

import org.jetbrains.skia.Point

operator fun Point.div(value: Number): Point {
    return Point(this.x / value.toFloat(), this.y / value.toFloat())
}