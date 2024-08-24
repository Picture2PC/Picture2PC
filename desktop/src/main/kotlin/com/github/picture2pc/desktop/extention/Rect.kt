package com.github.picture2pc.desktop.extention

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

operator fun Rect.div(value: Number): Rect {
    return Rect(Offset.Zero, Offset(this.width / value.toFloat(), this.height / value.toFloat()))
}