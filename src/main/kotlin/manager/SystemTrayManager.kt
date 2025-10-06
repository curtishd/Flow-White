package me.cdh.manager

import java.awt.Image
import java.awt.SystemTray
import java.awt.TrayIcon
import javax.imageio.ImageIO

object SystemTrayManager {
    private lateinit var trayIcon: TrayIcon
    private lateinit var systemTray: SystemTray
    fun initialize() {
        if (!SystemTray.isSupported()) return
        systemTray = SystemTray.getSystemTray()
        trayIcon = TrayIcon(
            ImageIO.read(javaClass.classLoader.getResourceAsStream("kotlin.png")).getScaledInstance(
                systemTray.trayIconSize.width, systemTray.trayIconSize.height, Image.SCALE_SMOOTH
            ), "Flow-White"
        )
        systemTray.add(trayIcon)
    }

    fun showTimeUpNotification() {
        if (SystemTray.isSupported() && ::trayIcon.isInitialized) {
            trayIcon.displayMessage(
                "Time's up!", "Countdown ended.", TrayIcon.MessageType.INFO
            )
        }
    }
}