package com.github.picture2pc.desktop.extention

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import com.github.picture2pc.desktop.data.RotationState
import kotlin.math.pow
import kotlin.math.sqrt

fun Offset.translate(
    rotationState: RotationState,
    bitmapBounds: Rect
): Offset {
    return when (rotationState) {
        RotationState.ROTATION_0, RotationState.ROTATION_180 -> this
        RotationState.ROTATION_90, RotationState.ROTATION_270 -> Offset(
            bitmapBounds.width - this.x,
            bitmapBounds.height - this.y
        )
    }
}

fun Offset.isInBounds(rectangle: IntRect): Boolean {
    return !(
            this.x < rectangle.left ||
                    this.x > rectangle.right ||
                    this.y < rectangle.top ||
                    this.y > rectangle.bottom
            )
}

fun Offset.distanceTo(secondPair: Offset): Float {
    return sqrt(
        (secondPair.x - this.x).pow(2) + (secondPair.y - this.y).pow(2)
    )
}

operator fun Offset.div(value: Number): Offset {
    return Offset(this.x / value.toFloat(), this.y / value.toFloat())
}

operator fun Offset.times(value: Number): Offset {
    return Offset(this.x * value.toFloat(), this.y * value.toFloat())
}

operator fun Offset.minus(size: IntSize): Offset {
    return Offset(
        this.x - (size.width.toFloat() / 2),
        this.y - (size.height.toFloat() / 2)
    )
}