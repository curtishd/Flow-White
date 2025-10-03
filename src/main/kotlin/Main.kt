package me.cdh

import com.formdev.flatlaf.themes.FlatMacLightLaf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import me.cdh.FlowContainer.menu
import javax.swing.SwingUtilities

object Main {
    val container = FlowContainer
    val timeLabel = FlowLabel

    init {
        container.contentPane.add(timeLabel)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            FlatMacLightLaf.setup()
            launch(Dispatchers.Swing) {
                SwingUtilities.updateComponentTreeUI(menu)
                FlowContainer.initSystemTray()
                container.isVisible = true
            }
            launch {
                while (isActive) {
                    System.gc()
                    delay(10000L)
                }
            }
        }
    }
}