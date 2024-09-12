package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.desktop.ui.constants.Descriptions
import com.github.picture2pc.desktop.ui.constants.Settings

@Composable
fun Sidebar() {
    val showConnections = remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxHeight()
            .width(Settings.SIDEBAR_WIDTH.dp)
            .background(Colors.SECONDARY, Shapes.WINDOW)
    ) {
        // Items in the Sidebar
        Column(Modifier.padding(Spacers.NORMAL).fillMaxSize()) {
            Header()
            Spacer(Modifier.height(Spacers.LARGE))

            Row { ImageInteractionButtons() }
            Spacer(Modifier.height(Spacers.LARGE))

            // CONNECTION INFO
            if (showConnections.value) {
                ConnectionInfo(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Colors.ACCENT, Shapes.BUTTON)
                )
                Spacer(Modifier.height(Spacers.NORMAL))
            } else Spacer(Modifier.weight(1f))

            // CONNECTION INFO TOGGLE BUTTON
            Box(Modifier.fillMaxWidth()) {
                TooltipIconButton(
                    Modifier.align(Alignment.BottomEnd),
                    Descriptions.INFO,
                    Icons.Desktop.INFO,
                    Colors.ACCENT,
                ) { showConnections.value = !showConnections.value }
            }
        }
    }
}