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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Icons.Desktop
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.desktop.data.next
import com.github.picture2pc.desktop.ui.constants.Descriptions
import com.github.picture2pc.desktop.ui.main.elements.Picture
import com.github.picture2pc.desktop.ui.main.elements.Sidebar
import com.github.picture2pc.desktop.ui.main.elements.TooltipIconButton
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject


@Composable
fun MainScreen(
    pDVM: PictureDisplayViewModel = rememberKoinInject()
) {
    val draggingSpeed = remember { pDVM.movementHandler.draggingSpeed }

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

            // Picture Display Area
            Box(Modifier.fillMaxSize()) {
                // Picture Display
                Column(
                    Modifier.border(
                        Borders.BORDER_STANDARD,
                        Colors.PRIMARY,
                        Shapes.WINDOW
                    )
                ) {
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
                            description = Descriptions.ROTATE_LEFT,
                            icon = Desktop.ROTATE_LEFT,
                            color = Colors.ACCENT,
                        ) {
                            pDVM.rotationState.value =
                                pDVM.rotationState.value.next(false)
                        }
                        Spacer(Modifier.width(Spacers.SMALL))

                        TooltipIconButton(
                            description = Descriptions.ROTATE_RIGHT,
                            icon = Desktop.ROTATE_RIGHT,
                            color = Colors.ACCENT,
                        ) {
                            pDVM.rotationState.value =
                                pDVM.rotationState.value.next(true)
                        }
                    }
                }

                // Zoom Speed Buttons
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(-Spacers.NORMAL, Spacers.NORMAL)
                ) {
                    Row {
                        TooltipIconButton(
                            description = Descriptions.DRAGGING_SPEED,
                            icon = draggingSpeed.value.iconPath,
                            color = Colors.ACCENT,
                        ) {
                            draggingSpeed.value = draggingSpeed.value.next()
                        }
                    }
                }
            }
        }
    }
}