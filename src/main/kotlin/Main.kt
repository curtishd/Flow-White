package me.cdh

import com.formdev.flatlaf.themes.FlatMacLightLaf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import me.cdh.container.FlowContainer

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking(Dispatchers.Swing) {
            FlatMacLightLaf.setup()
            launch {
                FlowContainer.isVisible = true
            }
        }
    }
}