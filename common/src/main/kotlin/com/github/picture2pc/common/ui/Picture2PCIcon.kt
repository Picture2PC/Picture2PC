package com.github.picture2pc.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
fun Picture2PCIcon(png: Boolean = false): Painter {
    return if (png) painterResource("icons/icon.png")
    else painterResource("icons/icon.svg")
}