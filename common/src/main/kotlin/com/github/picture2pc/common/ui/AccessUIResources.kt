package com.github.picture2pc.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

val Colors = Style.Colors
val Shapes = Style.Shapes
val StateColors = Style.Colors.States
val TextStyles = Style.TextStyles
val Spacers = Style.Dimensions.Spacers
val Heights = Style.Dimensions.Heights
val Borders = Style.Dimensions.Borders

@Composable
fun getIcon(path: String) = painterResource(path)