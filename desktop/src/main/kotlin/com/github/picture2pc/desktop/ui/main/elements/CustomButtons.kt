package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.desktop.ui.util.getIcon

@Composable
fun TooltipIconButton(
    description: String,
    icon: String,
    color: Color = Colors.PRIMARY,
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Tooltip(description, modifier) {
        IconButton(
            onClick,
            buttonModifier.background(color, Shapes.BUTTON)
        ) {
            Icon(
                getIcon(icon),
                description,
                tint = Colors.TEXT
            )
        }
    }
}