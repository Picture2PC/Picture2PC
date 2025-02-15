package com.github.picture2pc.android.ui.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

fun clamp(value: Float, min: Float, max: Float): Float {
    return when {
        value < min -> min
        value > max -> max
        else -> value
    }
}

fun clampInRect(screen: Rect, point: Offset): Offset {
    return Offset(
        clamp(point.x, screen.left, screen.right),
        clamp(point.y, screen.top, screen.bottom)
    )
}