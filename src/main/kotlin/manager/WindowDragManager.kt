package me.cdh.manager

import me.cdh.container.FlowContainer
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

object WindowDragManager {
    private val mouseMotionAdapter = object : MouseAdapter() {
        override fun mouseDragged(e: MouseEvent?) {
            e ?: return
            FlowContainer.setLocation(
                e.locationOnScreen.x - FlowContainer.width / 2,
                e.locationOnScreen.y - FlowContainer.height / 2
            )
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
        FlowContainer.addMouseMotionListener(mouseMotionAdapter)
        FlowContainer.addMouseWheelListener(mouseWheelAdapter)
    }

    fun disableDragging() {
        FlowContainer.removeMouseMotionListener(mouseMotionAdapter)
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