package me.cdh.container

import me.cdh.manager.SystemTrayManager
import me.cdh.manager.WindowDragManager
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Toolkit
import javax.swing.JWindow

object FlowContainer : JWindow() {
    val label = FlowLabel()
    val dialog = FlowDialog()
    val glassEffect = FrostedGlassEffect()
    val countDown = FlowCountdown()
    var remainingTime = 0L

    init {
        type = Type.UTILITY
        setSize(250, 80)
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        setLocation(screenSize.width - size.width - 100, screenSize.height - size.height - 160)
        background = Color(0.0f, 0.0f, 0.0f, 0.0f)
        isAlwaysOnTop = true
        initializeComponents()
    }

    private fun initializeComponents() {
        contentPane.add(label)
        WindowDragManager.initializeMouseListeners()
        SystemTrayManager.initialize()
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        glassEffect.paint(g as Graphics2D)
    }

    @Suppress
    private fun readResolve(): Any = FlowContainer
}