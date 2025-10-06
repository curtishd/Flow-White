package me.cdh

import com.formdev.flatlaf.themes.FlatMacLightLaf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import me.cdh.container.FlowContainer
import me.cdh.manager.MenuManager
import javax.swing.SwingUtilities

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking(Dispatchers.Swing) {
            FlatMacLightLaf.setup()
            launch {
                SwingUtilities.updateComponentTreeUI(MenuManager.menu)
                FlowContainer.isVisible = true
            }
        }
    }
}