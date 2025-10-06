package me.cdh.manager

import java.awt.Image
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

object SystemTrayManager {
    private lateinit var trayIcon: TrayIcon

    fun initialize() {
        if (!SystemTray.isSupported()) return
        val systemTray = SystemTray.getSystemTray()
        val trayImage = Toolkit.getDefaultToolkit()
            .createImage(javaClass.classLoader.getResourceAsStream("flat-white.png")!!.readAllBytes())
        trayIcon = TrayIcon(trayImage.getScaledInstance(16,16,Image.SCALE_SMOOTH), "Flow-White")
        systemTray.add(trayIcon)
    }

    fun showTimeUpNotification() {
        if (SystemTray.isSupported()) {
            trayIcon.displayMessage(
                "Time's up!", "Countdown ended.", TrayIcon.MessageType.INFO
            )
        }
    }
}