package org.picture2pc.picture2pc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalColors = staticCompositionLocalOf<PictureColors> {
    error("No PictureColors provided")
}
val LocalTypes = staticCompositionLocalOf<Typography> {
    error("No PictureTypes provided")
}

object PictureTheme {
    val Colors: PictureColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val Typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypes.current
}

enum class Theme {
    Light, Dark
}

@Composable
fun Picture2PCTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    val colors = when (theme) {
        Theme.Dark -> darkTheme
        Theme.Light -> lightTheme
    }

    val currentColors = remember { colors.copy() }.apply { updateColors(colors) }

    CompositionLocalProvider(
        LocalColors provides currentColors,
        LocalTypes provides pictureTypes,
    ) {
        content()
    }
}