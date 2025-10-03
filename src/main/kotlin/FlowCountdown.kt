package me.cdh

import javax.swing.JMenu
import javax.swing.JMenuItem

object FlowCountdown {
    val oneMin = JMenuItem("1 min").apply {
        addActionListener { FlowDialog.startCountDown(6_0000L) }
    }
    val fiveMin = JMenuItem("5 min").apply {
        addActionListener { FlowDialog.startCountDown(30_0000L) }
    }
    val twentyFiveMin = JMenuItem("25 min").apply {
        addActionListener { FlowDialog.startCountDown(150_0000L) }
    }
    val anHour = JMenuItem("1 h").apply {
        addActionListener { FlowDialog.startCountDown(360_0000L) }
    }
    val threeHour = JMenuItem("3 h").apply {
        addActionListener { FlowDialog.startCountDown(1080_0000L) }
    }
    val custom = JMenuItem("Custom Countdown").apply {
        addActionListener { FlowDialog.dialog.isVisible = true }
    }

    fun initializeFontMenu(menu: JMenu) {
        menu.add(oneMin)
        menu.add(fiveMin)
        menu.add(twentyFiveMin)
        menu.add(anHour)
        menu.add(threeHour)
        menu.add(custom)
    }
}