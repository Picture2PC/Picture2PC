package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Data
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.data.next
import com.github.picture2pc.desktop.ui.constants.Descriptions
import com.github.picture2pc.desktop.ui.main.elements.ImageInteractionButtons
import com.github.picture2pc.desktop.ui.main.elements.Picture
import com.github.picture2pc.desktop.ui.main.elements.TooltipIconButton
import com.github.picture2pc.desktop.ui.main.elements.connectionInfo
import com.github.picture2pc.desktop.ui.util.getIcon
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun MainScreen(
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject(),
    pDVM: PictureDisplayViewModel = rememberKoinInject()
) {
    serversSectionViewModel.refreshServers() // Notify all Servers that the client is online
    val showConnections = remember { mutableStateOf(false) }

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
            // SIDEBAR
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(246.dp)
                    .background(Colors.SECONDARY, Shapes.WINDOW)
            ) {
                // Items in the Sidebar
                Column(Modifier.padding(Spacers.NORMAL)) {
                    // HEADER
                    Row {
                        Image(
                            getIcon(Icons.Logo.STANDARD),
                            "Logo",
                            Modifier.width(75.dp)
                        )
                        Spacer(Modifier.width(Spacers.LARGE))

                        Text(
                            Data.APP_NAME,
                            Modifier.align(Alignment.CenterVertically),
                            style = TextStyles.HEADER1
                        )
                    }
                    Spacer(Modifier.height(Spacers.LARGE))

                    /*UNUSED QUALITY SELECTOR
                    Row { QualitySelector() }
                    Spacer(Modifier.height(Spacers.SMALL))
                    */

                    // IMAGE INTERACTION BUTTONS
                    Row { ImageInteractionButtons() }
                    Spacer(Modifier.height(Spacers.NORMAL))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Colors.PRIMARY, Shapes.BUTTON),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TooltipIconButton(
                            description = Descriptions.DARK,
                            icon = Icons.Desktop.DARK,
                        ) {
                            pDVM.pP.invert()
                        }
                    }
                    Spacer(Modifier.height(Spacers.NORMAL))

                    // CONNECTION INFO
                    if (showConnections.value) {
                        Row(
                            Modifier
                                .background(Colors.ACCENT, Shapes.BUTTON)
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) { connectionInfo(serversSectionViewModel) }
                    }

                    // DEBUG BUTTONS
                    Button(serversSectionViewModel::refreshServers) {
                        Text("Refresh Servers")
                    }
                    Row {
                        Button({ pDVM.loadTestImage() }) { Text("Load Test Image") }
                        Checkbox(
                            pDVM.isSelectPicture.value,
                            { pDVM.isSelectPicture.value = !pDVM.isSelectPicture.value },
                            Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    // CONNECTION INFO TOGGLE BUTTON
                    Box(Modifier.fillMaxSize()) {
                        TooltipIconButton(
                            description = Descriptions.INFO,
                            icon = Icons.Desktop.INFO,
                            color = Colors.ACCENT,
                            modifier = Modifier.align(Alignment.BottomEnd),
                        ) { showConnections.value = !showConnections.value }
                    }
                }
            }
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