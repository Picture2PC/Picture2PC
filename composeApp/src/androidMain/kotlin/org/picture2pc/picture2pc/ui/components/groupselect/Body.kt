package org.picture2pc.picture2pc.ui.components.groupselect

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Outline
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors

@Composable
fun GroupSelectBody() {
    Row(
        Modifier
            .fillMaxSize()
            .background(Colors.background, CornerRadius.Default)
            .border(
                BorderStroke(Outline.Container, Colors.primary),
                CornerRadius.Default
            )
    ) {

    }
}