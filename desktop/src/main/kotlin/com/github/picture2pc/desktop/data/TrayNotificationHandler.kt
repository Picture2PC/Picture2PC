package com.github.picture2pc.desktop.data

import com.github.picture2pc.common.ui.NotificationHandler
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.SystemTray
import java.awt.TrayIcon

class TrayNotificationHandler : NotificationHandler {
    override val showNotification: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun displayNotification(title: String, message: String, popup: Boolean) {
        showNotification.value = true
        if (SystemTray.isSupported() && !popup) {
            SystemTray.getSystemTray().trayIcons[0].displayMessage(
                title,
                message,
                TrayIcon.MessageType.INFO
            )
        }
    }
}