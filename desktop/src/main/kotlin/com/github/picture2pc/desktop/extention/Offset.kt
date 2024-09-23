package com.github.picture2pc.desktop.extention

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.github.picture2pc.desktop.data.RotationState
import kotlin.math.pow
import kotlin.math.sqrt

fun Offset.translate(
    rotationState: RotationState
): Offset {
    return when (rotationState) {
        RotationState.ROTATION_0, RotationState.ROTATION_180 -> this
        RotationState.ROTATION_90, RotationState.ROTATION_270 -> Offset(
            this.x * -1,
            this.y * -1
        )
    }
}

fun Offset.normalize(maxSize: Size): Offset {
    return Offset(
        this.x / maxSize.width,
        this.y / maxSize.height
    )
}

fun Offset.denormalize(maxSize: Size): Offset {
    return Offset(
        this.x * maxSize.width,
        this.y * maxSize.height
    )
}

fun Offset.isInBounds(rectangle: Rect): Boolean {
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

fun Offset.toCenteredOrigin(size: Size): Offset {
    return Offset(
        this.x - (size.width / 2),
        this.y - (size.height / 2)
    )
}

fun Offset.toTopLeftOrigin(size: Size): Offset {
    return Offset(
        this.x + (size.width / 2),
        this.y + (size.height / 2)
    )
}

operator fun Offset.div(value: Number): Offset {
    return Offset(this.x / value.toFloat(), this.y / value.toFloat())
}

operator fun Offset.times(value: Number): Offset {
    return Offset(this.x * value.toFloat(), this.y * value.toFloat())
}

operator fun Offset.minus(size: Size): Offset {
    return Offset(
        this.x - (size.width / 2),
        this.y - (size.height / 2)
    )
}

operator fun Offset.plus(i: Int): Offset {
    return Offset(this.x + i, this.y + i)
}