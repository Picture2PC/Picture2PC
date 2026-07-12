package org.picture2pc.picture2pc.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.picture2pc.picture2pc.ui.theme.PictureTheme

@Composable
fun PictureButton(
    modifier: Modifier = Modifier,
    type: ButtonVariant,
    onClick: () -> Unit,
    text: String = "",
    disabled: Boolean = false,
    selected: Boolean = false,
) {
    val buttonColor = when (type) {
        ButtonVariant.Primary ->
            ButtonDefaults.buttonColors(
                containerColor = PictureTheme.Colors.primary,
                contentColor = PictureTheme.Colors.text,
                disabledContainerColor = PictureTheme.Colors.primaryDisabled,
                disabledContentColor = PictureTheme.Colors.textDisabled,
            )

        ButtonVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = PictureTheme.Colors.secondary,
            contentColor = PictureTheme.Colors.text,
            disabledContainerColor = PictureTheme.Colors.secondaryDisabled,
            disabledContentColor = PictureTheme.Colors.textDisabled,
        )

        else -> error("Unsupported button type")
    }

    when {
        selected -> {
            OutlinedButton(
                modifier = modifier.height(35.dp),
                onClick = onClick,
                enabled = !disabled,
                colors = buttonColor,
                border = BorderStroke(2.dp, PictureTheme.Colors.accent),
            ) {
                Text(text, style = PictureTheme.Typography.labelLarge)
            }
        }

        else -> {
            Button(
                modifier = modifier.height(35.dp),
                onClick = onClick,
                enabled = !disabled,
                colors = buttonColor,
            ) {
                Text(text, style = PictureTheme.Typography.labelLarge)
            }
        }
    }
}

@Composable
fun PictureIconButton(
    modifier: Modifier = Modifier,
    type: ButtonVariant,
    onClick: () -> Unit,
    icon: DrawableResource,
    iconDescription: String,
    disabled: Boolean = false,
    selected: Boolean = false,
) {
    val buttonColor = when (type) {
        ButtonVariant.Primary -> IconButtonDefaults.iconButtonColors(
            containerColor = PictureTheme.Colors.primary,
            contentColor = PictureTheme.Colors.text,
            disabledContainerColor = PictureTheme.Colors.primaryDisabled,
            disabledContentColor = PictureTheme.Colors.textDisabled,
        )

        ButtonVariant.PrimaryDim -> IconButtonDefaults.iconButtonColors(
            containerColor = PictureTheme.Colors.primary.copy(alpha = 0.25f),
            contentColor = PictureTheme.Colors.text,
            disabledContainerColor = PictureTheme.Colors.primaryDisabled.copy(alpha = 0.25f),
            disabledContentColor = PictureTheme.Colors.textDisabled,
        )

        ButtonVariant.Secondary -> IconButtonDefaults.iconButtonColors(
            containerColor = PictureTheme.Colors.secondary,
            contentColor = PictureTheme.Colors.text,
            disabledContainerColor = PictureTheme.Colors.secondaryDisabled,
            disabledContentColor = PictureTheme.Colors.textDisabled,
        )

        ButtonVariant.Red -> IconButtonDefaults.iconButtonColors(
            containerColor = PictureTheme.Colors.error,
            contentColor = PictureTheme.Colors.text,
            disabledContainerColor = PictureTheme.Colors.errorBright,
            disabledContentColor = PictureTheme.Colors.textDisabled,
        )
    }

    when {
        selected -> {
            OutlinedIconButton(
                modifier = modifier.size(30.dp),
                onClick = onClick,
                enabled = !disabled,
                colors = buttonColor,
                border = BorderStroke(2.dp, PictureTheme.Colors.accent)
            ) {
                Icon(painterResource((icon)), contentDescription = iconDescription)
            }
        }

        else -> {
            IconButton(
                modifier = modifier.size(45.dp),
                onClick = onClick,
                enabled = !disabled,
                colors = buttonColor
            ) {
                Icon(painterResource((icon)), contentDescription = iconDescription)
            }
        }
    }
}
