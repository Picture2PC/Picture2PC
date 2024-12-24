package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.NotificationHandler
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.ui.main.elements.Picture
import com.github.picture2pc.desktop.ui.main.elements.PopupNotification
import com.github.picture2pc.desktop.ui.main.elements.RotationButtons
import com.github.picture2pc.desktop.ui.main.elements.Sidebar
import com.github.picture2pc.desktop.ui.main.elements.ZoomSpeedButton
import org.koin.compose.rememberKoinInject

@Composable
fun MainScreen(notificationHandler: NotificationHandler = rememberKoinInject<NotificationHandler>()) {
    val showNotification = notificationHandler.showNotification.collectAsState().value

    Box(
        Modifier
            .fillMaxSize()
            .background(Colors.BACKGROUND)
    ) {
        Row(
            Modifier.padding(10.dp).fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .width(Settings.SIDEBAR_WIDTH.dp)
                    .background(Colors.SECONDARY, Shapes.WINDOW)
            ) { Sidebar() }
            Spacer(Modifier.width(Spacers.NORMAL))

            Box(Modifier.fillMaxSize()) {
                // Picture Display
                Box(
                    Modifier.border(
                        Borders.BORDER_STANDARD,
                        Colors.PRIMARY,
                        Shapes.WINDOW
                    )
                        .fillMaxSize()
                        .padding(Spacers.NORMAL),
                    Alignment.Center
                ) { Picture() }

                // Notification for received picture
                if (showNotification) {
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(Spacers.LARGE)
                            .clickable { notificationHandler.showNotification.value = false }) {
                        PopupNotification()
                    }
                }

                Row(Modifier.padding(Spacers.NORMAL)) {
                    RotationButtons()
                    Spacer(Modifier.weight(1f))
                    ZoomSpeedButton()
                }
            }
        }
    }
}