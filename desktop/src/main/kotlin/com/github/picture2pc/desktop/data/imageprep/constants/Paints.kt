package com.github.picture2pc.desktop.data.imageprep.constants

import androidx.compose.ui.graphics.toArgb
import com.github.picture2pc.common.ui.Colors
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode

object Paints {
    val STROKE = Paint().apply {
        color = Colors.PRIMARY.toArgb()
        mode = PaintMode.STROKE
    }

    val FILL = Paint().apply {
        color = Colors.SECONDARY.toArgb()
        mode = PaintMode.FILL
    }
}