package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.desktop.ui.main.elements.PictureDisplay
import com.github.picture2pc.desktop.ui.main.elements.Sidebar

@Composable
fun MainScreen() {
    Box(Modifier.fillMaxSize().background(Colors.BACKGROUND)) {
        Row(
            Modifier.padding(10.dp).fillMaxSize()
        ) {
            Column { Sidebar() }
            Spacer(Modifier.width(Spacers.NORMAL))
            Column(
                Modifier.weight(1f).border(
                    Borders.BORDER_STANDARD,
                    Colors.PRIMARY,
                    Shapes.WINDOW
                )
            ) { PictureDisplay() }
        }
    }
}