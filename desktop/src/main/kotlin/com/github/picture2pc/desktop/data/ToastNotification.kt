package com.github.picture2pc.desktop.data

import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import javax.swing.JOptionPane
import java.util.Timer
import java.util.TimerTask

class Toastnotification {
    private val tray = SystemTray.getSystemTray()
    private val trayIcon = TrayIcon(Toolkit.getDefaultToolkit().createImage("Picture2PC.png"), "Desktop Notification")

    init {
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = "Picture2PC"
    }

    fun displayNotification() {
        if (SystemTray.isSupported()) {
            tray.add(trayIcon)
            trayIcon.displayMessage("Picture2PC", "Image received", TrayIcon.MessageType.INFO)
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    tray.remove(trayIcon)
                }
            }, 3000)
        }
        else {
            JOptionPane.showMessageDialog(
                null,
                "Image received",
                "Picture2PC",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }
}