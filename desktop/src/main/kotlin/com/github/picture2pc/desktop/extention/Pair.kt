package com.github.picture2pc.desktop.extention

import androidx.compose.ui.geometry.Rect
import com.github.picture2pc.desktop.data.RotationState
import org.jetbrains.skia.Point

fun Pair<Float, Float>.translate(
    rotationState: RotationState,
    bitmapBounds: Rect
): Pair<Float, Float> {
    return when (rotationState) {
        RotationState.ROTATION_0, RotationState.ROTATION_180 -> this
        RotationState.ROTATION_90, RotationState.ROTATION_270 -> Pair(
            bitmapBounds.width - this.first,
            bitmapBounds.height - this.second
        )
    }
}

fun Pair<Float, Float>.toPoint(): Point {
    return Point(this.first, this.second)
}

operator fun Pair<Float, Float>.div(value: Number): Pair<Float, Float> {
    return Pair(this.first / value.toFloat(), this.second / value.toFloat())
}