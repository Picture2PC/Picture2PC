package com.github.picture2pc.desktop.data

import com.github.picture2pc.common.ui.NotificationHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.awt.SystemTray
import java.awt.TrayIcon

class TrayNotificationHandler : NotificationHandler {
    private val _showNotification = MutableStateFlow(false)
    override val showNotification = _showNotification.asStateFlow()

    override fun displayNotification(title: String, message: String, popup: Boolean) {
        _showNotification.value = true
        if (SystemTray.isSupported() && !popup) {
            SystemTray.getSystemTray().trayIcons[0].displayMessage(
                title,
                message,
                TrayIcon.MessageType.INFO
            )
        }
    }

    override fun hideNotification() {
        _showNotification.value = false
    }
}