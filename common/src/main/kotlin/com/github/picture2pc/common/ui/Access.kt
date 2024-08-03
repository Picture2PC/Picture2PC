package com.github.picture2pc.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

val Colors = Style.Colors
val Shapes = Style.Shapes
val TextStyles = Style.TextStyles

@Composable
fun getIcon(path: String) = painterResource(path)