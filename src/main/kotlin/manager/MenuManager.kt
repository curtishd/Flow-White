package me.cdh.manager

import me.cdh.container.FlowContainer
import me.cdh.onClickAsync
import me.cdh.palette.RainbowColorPicker
import java.awt.Color
import java.awt.event.ItemEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JCheckBoxMenuItem
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import kotlin.system.exitProcess

object MenuManager {
    val menu: JPopupMenu by lazy(LazyThreadSafetyMode.NONE) {
        JPopupMenu().apply {
            add(createCountDownMenu())
            add(createChangeColorMenuItem())
            add(createFontMenu())
            add(createPinMenuItem())
            add(createTimeoutBehaviour())
            addSeparator()
            add(createExitMenuItem())
        }
    }

    fun createCountDownMenu(): JMenu {
        return JMenu("Count Down").apply {
            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    FlowContainer.countDown.initializeFontMenu(this@apply)
                }
            })
        }
    }

    fun createChangeColorMenuItem(): JMenuItem {
        return JMenuItem("Change Color").onClickAsync {
            RainbowColorPicker.showDialog(null, "Pick Color", Color.WHITE)?.let {
                FlowContainer.label.foreground = it
            }
        }
    }

    fun createFontMenu(): JMenu {
        return JMenu("Font Family").apply {
            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    FontMenuManager.initializeFontMenu(this@apply)
                }
            })
        }
    }

    fun createPinMenuItem(): JCheckBoxMenuItem {
        return JCheckBoxMenuItem("Pin").apply {
            addItemListener { e ->
                if (e.stateChange == ItemEvent.SELECTED) {
                    WindowDragManager.disableDragging()
                    FlowContainer.glassEffect.disable()
                } else if (e.stateChange == ItemEvent.DESELECTED) {
                    WindowDragManager.enableDragging()
                    FlowContainer.glassEffect.enable()
                }
            }
        }
    }

    fun createTimeoutBehaviour(): JMenu {
        return JMenu("Time out behaviour").apply {
            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    TimeoutManager.initialTimeoutMenu(this@apply)
                }
            })
        }
    }

    fun createExitMenuItem(): JMenuItem {
        return JMenuItem("Exit").onClickAsync {
            exitProcess(0)
        }
    }
}