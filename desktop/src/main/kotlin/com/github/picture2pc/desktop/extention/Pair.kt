package com.github.picture2pc.desktop.extention

import androidx.compose.ui.geometry.Rect
import com.github.picture2pc.desktop.data.RotationState
import kotlin.math.pow
import kotlin.math.sqrt

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

fun Pair<Float, Float>.isInBounds(rectangle: Rect): Boolean {
    return !(
            this.first < rectangle.left ||
                    this.first > rectangle.right ||
                    this.second < rectangle.top ||
                    this.second > rectangle.bottom
            )
}

fun Pair<Float, Float>.distanceTo(secondPair: Pair<Float, Float>): Float {
    return sqrt(
        (secondPair.first - this.first).pow(2) +
                (secondPair.second - this.second).pow(2)
    )
}

operator fun Pair<Float, Float>.div(value: Number): Pair<Float, Float> {
    return Pair(this.first / value.toFloat(), this.second / value.toFloat())
}