package com.github.picture2pc.desktop.data

enum class RotationState(val angle: Float) {
    ROTATION_0(0f),
    ROTATION_90(90f),
    ROTATION_180(180f),
    ROTATION_270(270f)
}

fun RotationState.next(clockwise: Boolean): RotationState {
    return when (this) {
        RotationState.ROTATION_0 ->
            if (clockwise) RotationState.ROTATION_90 else RotationState.ROTATION_270

        RotationState.ROTATION_90 ->
            if (clockwise) RotationState.ROTATION_180 else RotationState.ROTATION_0

        RotationState.ROTATION_180 ->
            if (clockwise) RotationState.ROTATION_270 else RotationState.ROTATION_90

        RotationState.ROTATION_270 ->
            if (clockwise) RotationState.ROTATION_0 else RotationState.ROTATION_180
    }
}