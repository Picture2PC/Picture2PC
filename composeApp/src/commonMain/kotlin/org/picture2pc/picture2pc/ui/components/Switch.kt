package org.picture2pc.picture2pc.ui.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import org.picture2pc.picture2pc.ui.theme.PictureTheme

@Composable
fun PictureSwitch(
    type: ButtonVariant,
    checked: Boolean,
    onCheckedChangeAction: (Boolean) -> Unit,
    enabled: Boolean = true,
    selected: Boolean = false,
) {
    val switchColors: SwitchColors = when (type) {
        ButtonVariant.Primary -> SwitchDefaults.colors(
            checkedThumbColor = PictureTheme.Colors.text,
            checkedTrackColor = PictureTheme.Colors.primary,
            checkedBorderColor = if (selected) PictureTheme.Colors.accent else PictureTheme.Colors.primary,
            checkedIconColor = PictureTheme.Colors.text,
            uncheckedThumbColor = PictureTheme.Colors.text,
            uncheckedTrackColor = PictureTheme.Colors.background,
            uncheckedBorderColor = if (selected) PictureTheme.Colors.accent else PictureTheme.Colors.primaryDisabled,
            uncheckedIconColor = PictureTheme.Colors.text,
            disabledCheckedThumbColor = PictureTheme.Colors.textDisabled,
            disabledCheckedTrackColor = PictureTheme.Colors.primaryDisabled,
            disabledCheckedBorderColor = if (selected) PictureTheme.Colors.accent else PictureTheme.Colors.primaryDisabled,
            disabledCheckedIconColor = PictureTheme.Colors.textDisabled,
            disabledUncheckedThumbColor = PictureTheme.Colors.textDisabled,
            disabledUncheckedTrackColor = PictureTheme.Colors.primaryDisabled,
            disabledUncheckedBorderColor = if (selected) PictureTheme.Colors.accent else PictureTheme.Colors.primaryDisabled,
            disabledUncheckedIconColor = PictureTheme.Colors.textDisabled
        )

        ButtonVariant.Secondary -> SwitchDefaults.colors(
            checkedThumbColor = PictureTheme.Colors.text,
            checkedTrackColor = PictureTheme.Colors.secondary,
            checkedBorderColor = if (selected) PictureTheme.Colors.accent else PictureTheme.Colors.secondary,
            checkedIconColor = PictureTheme.Colors.text,
            uncheckedThumbColor = PictureTheme.Colors.text,
            uncheckedTrackColor = PictureTheme.Colors.secondary,
            uncheckedBorderColor = if (selected) PictureTheme.Colors.accent else PictureTheme.Colors.secondaryDisabled,
            uncheckedIconColor = PictureTheme.Colors.text,
            disabledCheckedThumbColor = PictureTheme.Colors.textDisabled,
            disabledCheckedTrackColor = PictureTheme.Colors.secondaryDisabled,
            disabledCheckedBorderColor = if (selected) PictureTheme.Colors.accent else PictureTheme.Colors.secondaryDisabled,
            disabledCheckedIconColor = PictureTheme.Colors.textDisabled,
            disabledUncheckedThumbColor = PictureTheme.Colors.textDisabled,
            disabledUncheckedTrackColor = PictureTheme.Colors.secondaryDisabled,
            disabledUncheckedBorderColor = if (selected) PictureTheme.Colors.accent else PictureTheme.Colors.secondaryDisabled,
            disabledUncheckedIconColor = PictureTheme.Colors.textDisabled
        )

        else -> error("Unsupported switch type")
    }

    Switch(
        checked = checked,
        onCheckedChange = { newValue -> onCheckedChangeAction(newValue) },
        enabled = enabled,
        colors = switchColors
    )
}