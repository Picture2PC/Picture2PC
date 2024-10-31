package com.github.picture2pc.desktop.data

import java.awt.SystemTray
import java.awt.TrayIcon
import javax.swing.JOptionPane

class ToastNotification {
    fun displayNotification() {
        if (SystemTray.isSupported()) {
            val trayIcon = SystemTray.getSystemTray().trayIcons
            trayIcon[0].displayMessage(
                "Picture2PC",
                "Image received",
                TrayIcon.MessageType.INFO
            )
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Image received",
                "Picture2PC",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }
}