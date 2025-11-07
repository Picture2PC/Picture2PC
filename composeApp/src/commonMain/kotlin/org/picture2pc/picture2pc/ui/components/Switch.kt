package org.picture2pc.picture2pc.ui.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import org.picture2pc.picture2pc.ui.theme.PictureTheme

@Composable
fun PictureSwitch(
    type: ButtonType,
    checked: Boolean,
    onCheckedChangeAction: (Boolean) -> Unit,
    enabled: Boolean = true,
    selected: Boolean = false,
) {
    val switchColors: SwitchColors = when (type) {
        ButtonType.Primary -> SwitchDefaults.colors(
            checkedThumbColor = PictureTheme.colors.text,
            checkedTrackColor = PictureTheme.colors.primary,
            checkedBorderColor = if (selected) PictureTheme.colors.accent else PictureTheme.colors.primary,
            checkedIconColor = PictureTheme.colors.text,
            uncheckedThumbColor = PictureTheme.colors.text,
            uncheckedTrackColor = PictureTheme.colors.background,
            uncheckedBorderColor = if (selected) PictureTheme.colors.accent else PictureTheme.colors.primaryDisabled,
            uncheckedIconColor = PictureTheme.colors.text,
            disabledCheckedThumbColor = PictureTheme.colors.textDisabled,
            disabledCheckedTrackColor = PictureTheme.colors.primaryDisabled,
            disabledCheckedBorderColor = if (selected) PictureTheme.colors.accent else PictureTheme.colors.primaryDisabled,
            disabledCheckedIconColor = PictureTheme.colors.textDisabled,
            disabledUncheckedThumbColor = PictureTheme.colors.textDisabled,
            disabledUncheckedTrackColor = PictureTheme.colors.primaryDisabled,
            disabledUncheckedBorderColor = if (selected) PictureTheme.colors.accent else PictureTheme.colors.primaryDisabled,
            disabledUncheckedIconColor = PictureTheme.colors.textDisabled
        )

        ButtonType.Secondary -> SwitchDefaults.colors(
            checkedThumbColor = PictureTheme.colors.text,
            checkedTrackColor = PictureTheme.colors.secondary,
            checkedBorderColor = if (selected) PictureTheme.colors.accent else PictureTheme.colors.secondary,
            checkedIconColor = PictureTheme.colors.text,
            uncheckedThumbColor = PictureTheme.colors.text,
            uncheckedTrackColor = PictureTheme.colors.secondary,
            uncheckedBorderColor = if (selected) PictureTheme.colors.accent else PictureTheme.colors.secondaryDisabled,
            uncheckedIconColor = PictureTheme.colors.text,
            disabledCheckedThumbColor = PictureTheme.colors.textDisabled,
            disabledCheckedTrackColor = PictureTheme.colors.secondaryDisabled,
            disabledCheckedBorderColor = if (selected) PictureTheme.colors.accent else PictureTheme.colors.secondaryDisabled,
            disabledCheckedIconColor = PictureTheme.colors.textDisabled,
            disabledUncheckedThumbColor = PictureTheme.colors.textDisabled,
            disabledUncheckedTrackColor = PictureTheme.colors.secondaryDisabled,
            disabledUncheckedBorderColor = if (selected) PictureTheme.colors.accent else PictureTheme.colors.secondaryDisabled,
            disabledUncheckedIconColor = PictureTheme.colors.textDisabled
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