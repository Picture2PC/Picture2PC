package org.picture2pc.picture2pc.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.picture2pc.picture2pc.ui.theme.PictureTheme

enum class ButtonType {
    Primary,
    Secondary,
}

@Composable
fun PictureButton(
    type: ButtonType,
    onClick: () -> Unit,
    text: String = "",
    disabled: Boolean = false,
    selected: Boolean = false,
) {
    val buttonColor = when (type) {
        ButtonType.Primary ->
            ButtonDefaults.buttonColors(
                containerColor = PictureTheme.colors.primary,
                contentColor = PictureTheme.colors.text,
                disabledContainerColor = PictureTheme.colors.primaryDisabled,
                disabledContentColor = PictureTheme.colors.textDisabled,
            )

        ButtonType.Secondary -> ButtonDefaults.buttonColors(
            containerColor = PictureTheme.colors.secondary,
            contentColor = PictureTheme.colors.text,
            disabledContainerColor = PictureTheme.colors.secondaryDisabled,
            disabledContentColor = PictureTheme.colors.textDisabled,
        )
    }

    when {
        selected -> {
            OutlinedButton(
                onClick = onClick,
                enabled = !disabled,
                colors = buttonColor,
                border = BorderStroke(2.dp, PictureTheme.colors.accent)
            ) {
                Text(text, style = PictureTheme.typography.labelLarge)
            }
        }

        else -> {
            Button(
                onClick = onClick,
                enabled = !disabled,
                colors = buttonColor
            ) {
                Text(text, style = PictureTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun PictureIconButton(
    type: ButtonType,
    onClick: () -> Unit,
    icon: DrawableResource,
    iconDescription: String,
    disabled: Boolean = false,
    selected: Boolean = false,
) {
    val buttonColor = when (type) {
        ButtonType.Primary ->
            IconButtonDefaults.iconButtonColors(
                containerColor = PictureTheme.colors.primary,
                contentColor = PictureTheme.colors.text,
                disabledContainerColor = PictureTheme.colors.primaryDisabled,
                disabledContentColor = PictureTheme.colors.textDisabled,
            )

        ButtonType.Secondary -> IconButtonDefaults.iconButtonColors(
            containerColor = PictureTheme.colors.secondary,
            contentColor = PictureTheme.colors.text,
            disabledContainerColor = PictureTheme.colors.secondaryDisabled,
            disabledContentColor = PictureTheme.colors.textDisabled,
        )
    }

    when {
        selected -> {
            OutlinedIconButton(
                onClick = onClick,
                enabled = !disabled,
                colors = buttonColor,
                border = BorderStroke(2.dp, PictureTheme.colors.accent)
            ) {
                Icon(painterResource((icon)), contentDescription = iconDescription)
            }
        }

        else -> {
            IconButton(
                onClick = onClick,
                enabled = !disabled,
                colors = buttonColor
            ) {
                Icon(painterResource((icon)), contentDescription = iconDescription)
            }
        }
    }
}
