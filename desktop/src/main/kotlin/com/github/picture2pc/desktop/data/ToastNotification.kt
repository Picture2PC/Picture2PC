package com.github.picture2pc.desktop.data

import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.TrayIcon.MessageType

class Toastnotification {
    private val tray: SystemTray = SystemTray.getSystemTray()
    private val trayIcon: TrayIcon

    init {
        val image = Toolkit.getDefaultToolkit().createImage("Picture2PC.png")
        trayIcon = TrayIcon(image, "Picture2PC")
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = "Picture2PC"
    }

    fun displayNotification() {
        tray.add(trayIcon)
        trayIcon.displayMessage("Picture2PC", "Picture received!", MessageType.INFO)
    }
}