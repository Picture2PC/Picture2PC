package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.desktop.data.next
import com.github.picture2pc.desktop.ui.constants.Descriptions
import com.github.picture2pc.desktop.ui.main.elements.Picture
import com.github.picture2pc.desktop.ui.main.elements.Sidebar
import com.github.picture2pc.desktop.ui.main.elements.TooltipIconButton
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun MainScreen(
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject(),
    pDVM: PictureDisplayViewModel = rememberKoinInject()
) {
    serversSectionViewModel.refreshServers() // Notify all Servers that the client is online

    Box(
        Modifier
            .fillMaxSize()
            .background(Colors.BACKGROUND)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(Spacers.NORMAL)
        ) {
            Sidebar()
            Spacer(Modifier.width(Spacers.NORMAL))

            // PICTURE DISPLAY AREA
            Box(Modifier.fillMaxSize()) {
                // Picture Display
                Column(Modifier.border(Borders.BORDER_STANDARD, Colors.PRIMARY, Shapes.WINDOW)) {
                    Box(
                        Modifier
                            .rotate(pDVM.rotationState.value.angle)
                            .padding(Spacers.NORMAL)
                            .fillMaxSize(),
                        Alignment.Center
                    ) { Picture() }
                }

                // Rotation Buttons
                Box(Modifier.offset(Spacers.NORMAL, Spacers.NORMAL)) {
                    Row {
                        TooltipIconButton(
                            Descriptions.ROTATE_LEFT,
                            Icons.Desktop.ROTATE_LEFT,
                            Colors.ACCENT,
                        ) {
                            pDVM.rotationState.value =
                                pDVM.rotationState.value.next(false)
                        }

                        Spacer(Modifier.width(Spacers.SMALL))

                        TooltipIconButton(
                            Descriptions.ROTATE_RIGHT,
                            Icons.Desktop.ROTATE_RIGHT,
                            Colors.ACCENT,
                        ) {
                            pDVM.rotationState.value =
                                pDVM.rotationState.value.next(true)
                        }
                    }
                }
            }
        }
    }
}