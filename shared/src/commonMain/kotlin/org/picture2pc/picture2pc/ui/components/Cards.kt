package org.picture2pc.picture2pc.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.picture2pc.picture2pc.ui.theme.CornerRadius
import org.picture2pc.picture2pc.ui.theme.Outline
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.Spacer

@Composable
fun InnerCard(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(Spacer.Large),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Colors.background, CornerRadius.Default)
            .border(BorderStroke(Outline.Container, Colors.primary), CornerRadius.Default)
            .padding(Padding.Container),
        verticalArrangement = verticalArrangement,
        content = content
    )
}

@Composable
fun SecondaryCard(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(Spacer.Medium),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Colors.secondary, CornerRadius.Default)
            .padding(Padding.Container),
        verticalArrangement = verticalArrangement,
        content = content
    )
}

@Composable
fun ScreenCardContainer(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(Padding.Small)
            .border(BorderStroke(Outline.Container, Colors.primary), CornerRadius.Default)
            .background(Colors.secondary, CornerRadius.Default),
        content = content
    )
}