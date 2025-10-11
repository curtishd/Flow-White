package me.cdh.manager

import me.cdh.container.FlowContainer
import java.awt.Dimension
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

object WindowDragManager {
    private val mouseMotionAdapter = object : MouseAdapter() {
        private var dragOffset: Point? = null
        override fun mousePressed(e: MouseEvent) {
            dragOffset = Point(e.x, e.y)
        }

        override fun mouseDragged(e: MouseEvent) {
            dragOffset?.let {
                FlowContainer.setLocation(
                    e.locationOnScreen.x - it.x,
                    e.locationOnScreen.y - it.y
                )
            }
        }

        override fun mouseReleased(e: MouseEvent) {
            dragOffset = null
        }
    }
    private val mouseWheelAdapter = object : MouseAdapter() {
        override fun mouseWheelMoved(e: MouseWheelEvent) {
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
        FlowContainer.addMouseListener(mouseMotionAdapter)
        FlowContainer.addMouseWheelListener(mouseWheelAdapter)
    }

    fun disableDragging() {
        FlowContainer.removeMouseMotionListener(mouseMotionAdapter)
        FlowContainer.removeMouseListener(mouseMotionAdapter)
        FlowContainer.removeMouseWheelListener(mouseWheelAdapter)
    }

    fun initializeMouseListeners() {
        enableDragging()

        FlowContainer.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON3 || e.isPopupTrigger) {
                    MenuManager.menu.show(e.component, e.x, e.y)
                }
            }
        })
    }
}