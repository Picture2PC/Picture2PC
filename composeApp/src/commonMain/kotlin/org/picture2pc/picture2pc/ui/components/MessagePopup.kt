package org.picture2pc.picture2pc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Outline
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Typography
import org.picture2pc.picture2pc.ui.theme.Spacer

@Composable
fun MessagePopup(
    modifier: Modifier = Modifier,
    label: String,
    icon: DrawableResource? = null,
    description: String? = null,
    color: Color
) {
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        exit = fadeOut() + shrinkVertically()
    ) {
        Column(
            modifier = modifier
                .clip(CornerRadius.Default)
                .clickable { visible = false }
                .background(color.copy(0.5f), CornerRadius.Default)
                .border(Outline.Thin, color, CornerRadius.Default)
                .padding(Padding.Large)
        ) {

            if (icon != null) {
                Row {
                    Icon(
                        painterResource(icon),
                        contentDescription = icon.toString(),
                        tint = Colors.text
                    )
                    Spacer(Modifier.width(Spacer.Medium))
                    Text(label, color = Colors.text, style = Typography.titleMedium)
                }
                if (description != null) {
                    Spacer(Modifier.height(Spacer.Medium))
                    Text(description, color = Colors.text, style = Typography.labelLarge)
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(
                        label,
                        color = Colors.text,
                        style = Typography.titleMedium
                    )
                }
            }
        }
    }
}