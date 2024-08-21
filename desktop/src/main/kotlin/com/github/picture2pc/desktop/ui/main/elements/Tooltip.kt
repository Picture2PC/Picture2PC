package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.TextStyles

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tooltip(description: String, modifier: Modifier = Modifier, element: @Composable () -> Unit) {
    TooltipArea(
        {
            Box {
                Text(
                    description,
                    Modifier
                        .background(Colors.PRIMARY, Shapes.BUTTON)
                        .border(Borders.BORDER_STANDARD, Colors.ACCENT, Shapes.BUTTON)
                        .padding(Spacers.SMALL)
                        .align(Alignment.Center),
                    Colors.TEXT,
                    style = TextStyles.SMALL
                )
            }
        },
        modifier,
        tooltipPlacement = TooltipPlacement.CursorPoint(DpOffset(0.dp, 16.dp + Spacers.SMALL))
    ) { element() }
}