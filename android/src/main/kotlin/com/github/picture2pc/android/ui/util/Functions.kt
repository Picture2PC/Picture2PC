package com.github.picture2pc.android.ui.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

fun clampInRect(screen: Rect, point: Offset): Offset {
    return Offset(
        point.x.coerceIn(screen.left, screen.right),
        point.y.coerceIn(screen.top, screen.bottom)
    )
}