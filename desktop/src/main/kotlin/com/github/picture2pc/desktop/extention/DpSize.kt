package com.github.picture2pc.desktop.extention

import androidx.compose.ui.unit.DpSize
import com.github.picture2pc.desktop.data.RotationState

fun DpSize.transpose(rotationState: RotationState): DpSize {
    return if (rotationState.ordinal % 2 == 1)
        DpSize(height, width)
    else
        this
}