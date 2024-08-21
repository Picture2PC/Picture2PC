package com.github.picture2pc.desktop.data

enum class RotationStates(val angle: Float) {
    ROTATION_0(0f),
    ROTATION_90(90f),
    ROTATION_180(180f),
    ROTATION_270(270f)
}

fun RotationStates.next(clockwise: Boolean): RotationStates {
    return when (this) {
        RotationStates.ROTATION_0 ->
            if (clockwise) RotationStates.ROTATION_90 else RotationStates.ROTATION_270

        RotationStates.ROTATION_90 ->
            if (clockwise) RotationStates.ROTATION_180 else RotationStates.ROTATION_0

        RotationStates.ROTATION_180 ->
            if (clockwise) RotationStates.ROTATION_270 else RotationStates.ROTATION_90

        RotationStates.ROTATION_270 ->
            if (clockwise) RotationStates.ROTATION_0 else RotationStates.ROTATION_180
    }
}