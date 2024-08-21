package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.github.picture2pc.desktop.ui.main.elements.Tooltip
import com.github.picture2pc.desktop.ui.main.imagemanupulation.ImageInteractionButtons
import com.github.picture2pc.desktop.ui.main.picturedisplay.Picture
import com.github.picture2pc.desktop.ui.main.serverssection.connectionInfo
import com.github.picture2pc.desktop.ui.util.getIcon
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun MainScreen(
    serversSectionViewModel: ServersSectionViewModel = rememberKoinInject(),
    pDVM: PictureDisplayViewModel = rememberKoinInject()
) {
    // Notify all Servers that the client is online
    serversSectionViewModel.refreshServers()
    val showConnections = remember { mutableStateOf(false) }

    //TODO: Add tooltips to buttons

    Box(
        Modifier
            .background(Colors.BACKGROUND)
            .fillMaxSize()
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
                Column(Modifier.padding(Spacers.NORMAL)) {
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

                    Row { ImageInteractionButtons() }
                    Spacer(Modifier.height(Spacers.LARGE))

                    if (showConnections.value) {
                        Row(
                            Modifier
                                .background(Colors.ACCENT, Shapes.BUTTON)
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) { connectionInfo() }
                    }

                    // DEBUG BUTTONS
                    Button(serversSectionViewModel::refreshServers) {
                        Text("Refresh Servers")
                    }
                    Row {
                        Button({ pDVM.loadTestImage() }) {
                            Text("Load Test Image")
                        }
                        Checkbox(
                            pDVM.isSelectPicture.value,
                            { pDVM.isSelectPicture.value = !pDVM.isSelectPicture.value },
                            Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    Box(Modifier.fillMaxSize()) {
                        Tooltip(
                            Descriptions.INFO, Modifier
                                .align(Alignment.BottomEnd)
                        ) {
                            IconButton(
                                { showConnections.value = !showConnections.value },
                                Modifier
                                    .background(Colors.ACCENT, Shapes.BUTTON)
                            ) {
                                Icon(
                                    getIcon(Icons.Desktop.INFO),
                                    "Info",
                                    tint = Colors.TEXT
                                )
                            }
                        }

                    }
                }
            }
            Spacer(Modifier.width(Spacers.NORMAL))

            // PICTURE DISPLAY AREA
            Box(Modifier.fillMaxSize()) {
                Column(Modifier.border(Borders.BORDER_STANDARD, Colors.PRIMARY, Shapes.WINDOW)) {
                    Box(
                        Modifier
                            .rotate(pDVM.rotationState.value.angle)
                            .padding(Spacers.NORMAL)
                            .fillMaxSize(),
                        Alignment.Center
                    ) { Picture() }
                }
                Box(Modifier.offset(Spacers.NORMAL, Spacers.NORMAL)) {
                    Row {
                        IconButton(
                            {
                                pDVM.rotationState.value = pDVM.rotationState.value.next(false)
                            },
                            Modifier.background(Colors.ACCENT, Shapes.BUTTON)
                        ) {
                            Icon(
                                getIcon(Icons.Desktop.ROTATE_LEFT),
                                "Rotate Left",
                                tint = Colors.TEXT
                            )
                        }

                        Spacer(Modifier.width(Spacers.SMALL))

                        Row {
                            IconButton(
                                {
                                    pDVM.rotationState.value = pDVM.rotationState.value.next(true)
                                },
                                Modifier.background(Colors.ACCENT, Shapes.BUTTON)
                            ) {
                                Icon(
                                    getIcon(Icons.Desktop.ROTATE_RIGHT),
                                    "Rotate Right",
                                    tint = Colors.TEXT
                                )
                            }
                        }

                        Spacer(Modifier.width(Spacers.SMALL))

                        Row {
                            Tooltip(Descriptions.ROTATE_RIGHT) {
                                IconButton(
                                    {
                                        pDVM.rotationState.value =
                                            pDVM.rotationState.value.next(true)
                                    },
                                    Modifier.background(Colors.ACCENT, Shapes.BUTTON)
                                ) {
                                    Icon(
                                        getIcon(Icons.Desktop.ROTATE_RIGHT),
                                        "Rotate Right",
                                        tint = Colors.TEXT
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}