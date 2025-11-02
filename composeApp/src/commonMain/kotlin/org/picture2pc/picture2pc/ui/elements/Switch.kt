package org.picture2pc.picture2pc.ui.elements

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.picture2pc.picture2pc.ui.theme.PictureTheme

@Composable
fun PictureSwitch(
    type: ButtonType,
    defaultState: Boolean,
    onCheckedChangeAction: () -> Unit,
    enabled: Boolean = true,
    selected: Boolean = false,
) {
    val checkedState = remember { mutableStateOf(defaultState) }
    LaunchedEffect(defaultState) {
        checkedState.value = defaultState
    }

    val switchColors: SwitchColors = when (type) {
        ButtonType.Primary -> SwitchDefaults.colors(
            checkedThumbColor = PictureTheme.colors.text,
            checkedTrackColor = PictureTheme.colors.primary,
            checkedBorderColor = when {
                selected -> PictureTheme.colors.accent
                else -> PictureTheme.colors.primary
            },
            checkedIconColor = PictureTheme.colors.text,
            uncheckedThumbColor = PictureTheme.colors.text,
            uncheckedTrackColor = PictureTheme.colors.primary,
            uncheckedBorderColor = when {
                selected -> PictureTheme.colors.accent
                else -> PictureTheme.colors.primaryDisabled
            },
            uncheckedIconColor = PictureTheme.colors.text,
            disabledCheckedThumbColor = PictureTheme.colors.textDisabled,
            disabledCheckedTrackColor = PictureTheme.colors.primaryDisabled,
            disabledCheckedBorderColor = when {
                selected -> PictureTheme.colors.accent
                else -> PictureTheme.colors.primaryDisabled
            },
            disabledCheckedIconColor = PictureTheme.colors.textDisabled,
            disabledUncheckedThumbColor = PictureTheme.colors.textDisabled,
            disabledUncheckedTrackColor = PictureTheme.colors.primaryDisabled,
            disabledUncheckedBorderColor = when {
                selected -> PictureTheme.colors.accent
                else -> PictureTheme.colors.primaryDisabled
            },
            disabledUncheckedIconColor = PictureTheme.colors.textDisabled
        )

        ButtonType.Secondary -> SwitchDefaults.colors(
            checkedThumbColor = PictureTheme.colors.text,
            checkedTrackColor = PictureTheme.colors.secondary,
            checkedBorderColor = when {
                selected -> PictureTheme.colors.accent
                else -> PictureTheme.colors.secondary
            },
            checkedIconColor = PictureTheme.colors.text,
            uncheckedThumbColor = PictureTheme.colors.text,
            uncheckedTrackColor = PictureTheme.colors.secondary,
            uncheckedBorderColor = when {
                selected -> PictureTheme.colors.accent
                else -> PictureTheme.colors.secondaryDisabled
            },
            uncheckedIconColor = PictureTheme.colors.text,
            disabledCheckedThumbColor = PictureTheme.colors.textDisabled,
            disabledCheckedTrackColor = PictureTheme.colors.secondaryDisabled,
            disabledCheckedBorderColor = when {
                selected -> PictureTheme.colors.accent
                else -> PictureTheme.colors.secondaryDisabled
            },
            disabledCheckedIconColor = PictureTheme.colors.textDisabled,
            disabledUncheckedThumbColor = PictureTheme.colors.textDisabled,
            disabledUncheckedTrackColor = PictureTheme.colors.secondaryDisabled,
            disabledUncheckedBorderColor = when {
                selected -> PictureTheme.colors.accent
                else -> PictureTheme.colors.secondaryDisabled
            },
            disabledUncheckedIconColor = PictureTheme.colors.textDisabled
        )
    }

    Switch(
        checked = checkedState.value,
        onCheckedChange = { newChecked ->
            checkedState.value = newChecked
            onCheckedChangeAction()
        },
        enabled = enabled,
        colors = switchColors
    )
}