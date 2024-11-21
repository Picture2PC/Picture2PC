package com.github.picture2pc.desktop.data

import com.github.picture2pc.common.ui.NotificationHandler
import java.awt.SystemTray
import java.awt.TrayIcon

class TrayNotificationHandler : NotificationHandler {
    override fun displayNotification(title:String, message:String, popup:Boolean) {
        if (SystemTray.isSupported() && !popup) {
            SystemTray.getSystemTray().trayIcons[0].displayMessage(
                title,
                message,
                TrayIcon.MessageType.INFO
            )
            return}
        println("HelOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
    }
}