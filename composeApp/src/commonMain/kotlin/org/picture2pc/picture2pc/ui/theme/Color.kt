package org.picture2pc.picture2pc.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

private val receiving = Color(0xFF3557C8)
private val connected = Color(0xFF2FB86C)
private val pending = Color(0xFFFFE921)
private val accent = Color(0xFFFF6A00)
private val error = Color(0xFFC83538)
private val errorBright = Color(0xFFC26D6F)

private val textDark = Color(0xFFEEEAF8)
private val textDisabledDark = Color(0xFFBCB9C4)
private val highlightDark = Color(0xFFA996DB)
private val primaryDark = Color(0xFF6441BE)
private val primaryHighlightedDark = Color(0xFF7F53F2)
private val primaryDisabledDark = Color(0xFF8D7ABE)
private val secondaryDark = Color(0xFF372469)
private val secondaryHighlightedDark = Color(0xFF51359C)
private val secondaryDisabledDark = Color(0xFF4E4369)
private val backgroundDark = Color(0xFF0B0715)

private val textLight = Color(0xFF1A1A1A)
private val textDisabledLight = Color(0xFF555555)
private val highlightLight = Color(0xFF7D4AE0)
private val primaryLight = Color(0xFF8C6EF6)
private val primaryHighlightedLight = Color(0xFFB299FF)
private val primaryDisabledLight = Color(0xFF7B5DE8)
private val secondaryLight = Color(0xFF6B54C7)
private val secondaryHighlightedLight = Color(0xFF9768FF)
private val secondaryDisabledLight = Color(0xFF7A70B5)
private val backgroundLight = Color(0xFFF5F2FF)

class PictureColors(
    text: Color,
    textDisabled: Color,
    highlight: Color,
    primary: Color,
    primaryHighlighted: Color,
    primaryDisabled: Color,
    secondary: Color,
    secondaryHighlighted: Color,
    secondaryDisabled: Color,
    background: Color,
    val accent: Color = org.picture2pc.picture2pc.ui.theme.accent,
    val error: Color = org.picture2pc.picture2pc.ui.theme.error,
    val errorBright: Color = org.picture2pc.picture2pc.ui.theme.errorBright,
    val blue: Color = org.picture2pc.picture2pc.ui.theme.receiving,
    val connected: Color = org.picture2pc.picture2pc.ui.theme.connected,
    val pending: Color = org.picture2pc.picture2pc.ui.theme.pending,
) {
    var text by mutableStateOf(text)
        private set
    var textDisabled by mutableStateOf(textDisabled)
        private set
    var highlight by mutableStateOf(highlight)
        private set
    var primary by mutableStateOf(primary)
        private set
    var primaryHighlighted by mutableStateOf(primaryHighlighted)
        private set
    var primaryDisabled by mutableStateOf(primaryDisabled)
        private set
    var secondary by mutableStateOf(secondary)
        private set
    var secondaryHighlighted by mutableStateOf(secondaryHighlighted)
        private set
    var secondaryDisabled by mutableStateOf(secondaryDisabled)
        private set
    var background by mutableStateOf(background)
        private set

    fun copy(): PictureColors {
        return PictureColors(
            text = text,
            textDisabled = textDisabled,
            highlight = highlight,
            primary = primary,
            primaryHighlighted = primaryHighlighted,
            primaryDisabled = primaryDisabled,
            secondary = secondary,
            secondaryHighlighted = secondaryHighlighted,
            secondaryDisabled = secondaryDisabled,
            background = background,
            accent = accent,
            error = error,
            errorBright = errorBright,
            blue = blue,
            connected = connected,
            pending = pending,
        )
    }

    fun updateColors(other: PictureColors) {
        text = other.text
        textDisabled = other.textDisabled
        highlight = other.highlight
        primary = other.primary
        primaryHighlighted = other.primaryHighlighted
        primaryDisabled = other.primaryDisabled
        secondary = other.secondary
        secondaryHighlighted = other.secondaryHighlighted
        secondaryDisabled = other.secondaryDisabled
        background = other.background
    }
}

val darkTheme = PictureColors(
    text = textDark,
    textDisabled = textDisabledDark,
    highlight = highlightDark,
    primary = primaryDark,
    primaryHighlighted = primaryHighlightedDark,
    primaryDisabled = primaryDisabledDark,
    secondary = secondaryDark,
    secondaryHighlighted = secondaryHighlightedDark,
    secondaryDisabled = secondaryDisabledDark,
    background = backgroundDark,
)

val lightTheme = PictureColors(
    text = textLight,
    textDisabled = textDisabledLight,
    highlight = highlightLight,
    primary = primaryLight,
    primaryHighlighted = primaryHighlightedLight,
    primaryDisabled = primaryDisabledLight,
    secondary = secondaryLight,
    secondaryHighlighted = secondaryHighlightedLight,
    secondaryDisabled = secondaryDisabledLight,
    background = backgroundLight,
)

object ConnectionColors {
    val Receiving = receiving
    val Connected = connected
    val Pending = pending
    val Disconnected = error
    val Error = error
}