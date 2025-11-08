package me.cdh.manager

import me.cdh.container.FlowContainer
import java.awt.Dimension
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

object WindowDragManager {
    private val motionAdapter = object : MouseAdapter() {
        var dragOffset: Point? = null
        override fun mousePressed(e: MouseEvent?) {
            e ?: return
            dragOffset = Point(e.x, e.y)
        }

        override fun mouseDragged(e: MouseEvent?) {
            e ?: return
            dragOffset?.let {
                FlowContainer.setLocation(e.locationOnScreen.x - it.x, e.locationOnScreen.y - it.y)
            }
        }

        override fun mouseReleased(e: MouseEvent?) {
            dragOffset = null
        }
    }
    private val mouseWheelAdapter = object : MouseAdapter() {
        override fun mouseWheelMoved(e: MouseWheelEvent?) {
            e ?: return
            val rotation = e.wheelRotation
            val currentFontSize = FlowContainer.label.font.size
            val newFontSize = currentFontSize + rotation

            val clampFontSize = newFontSize.coerceIn(8, 72)
            if (clampFontSize != currentFontSize) {
                FlowContainer.label.font = FlowContainer.label.font.deriveFont(clampFontSize.toFloat())
                adjustWindowSize()
            }
        }

        private fun adjustWindowSize() {
            val labelPreferredSize = FlowContainer.label.preferredSize
            val newWidth = (labelPreferredSize.width * 1.2).toInt()
            val newHeight = (labelPreferredSize.height * 1.2).toInt()
            FlowContainer.size = Dimension(newWidth, newHeight)
            FlowContainer.revalidate()
            FlowContainer.repaint()
        }
    }

    fun enableDragging() {
        FlowContainer.addMouseMotionListener(motionAdapter)
        FlowContainer.addMouseListener(motionAdapter)
        FlowContainer.addMouseWheelListener(mouseWheelAdapter)
    }

    fun disableDragging() {
        FlowContainer.removeMouseMotionListener(motionAdapter)
        FlowContainer.removeMouseListener(motionAdapter)
        FlowContainer.removeMouseWheelListener(mouseWheelAdapter)
    }

    fun initializeMouseListeners() {
        enableDragging()

        FlowContainer.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent?) {
                e ?: return
                if (e.button == MouseEvent.BUTTON3 || e.isPopupTrigger) {
                    MenuManager.menu.show(e.component, e.x, e.y)
                }
            }
        })
    }
}