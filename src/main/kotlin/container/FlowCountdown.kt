package me.cdh.container

import me.cdh.onClickAsync
import javax.swing.JMenu
import javax.swing.JMenuItem

class FlowCountdown {
    private var isInitialized = false
    val oneMin = JMenuItem("1 min").onClickAsync {
        FlowContainer.dialog.startCountDown(6_0000L)
    }
    val fiveMin = JMenuItem("5 min").onClickAsync {
        FlowContainer.dialog.startCountDown(30_0000L)
    }
    val twentyFiveMin = JMenuItem("25 min").onClickAsync {
        FlowContainer.dialog.startCountDown(150_0000L)
    }
    val anHour = JMenuItem("1 h").onClickAsync {
        FlowContainer.dialog.startCountDown(360_0000L)
    }
    val threeHour = JMenuItem("3 h").onClickAsync {
        FlowContainer.dialog.startCountDown(1080_0000L)
    }
    val custom = JMenuItem("Custom Countdown").onClickAsync {
        FlowContainer.dialog.isVisible = true
    }

    fun initializeFontMenu(menu: JMenu) {
        if (isInitialized) return
        menu.add(oneMin)
        menu.add(fiveMin)
        menu.add(twentyFiveMin)
        menu.add(anHour)
        menu.add(threeHour)
        menu.addSeparator()
        menu.add(custom)
        isInitialized = true
    }
}