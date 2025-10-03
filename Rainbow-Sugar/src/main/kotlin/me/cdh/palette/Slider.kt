package me.cdh.palette

import com.formdev.flatlaf.util.UIScale
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.math.max
import kotlin.math.min

abstract class Slider : JComponent() {
    open fun install() {
        val mouseListener: MouseAdapter = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mouseChange(e.getPoint())
                }
            }

            override fun mouseDragged(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mouseChange(e.getPoint())
                }
            }
        }
        addMouseListener(mouseListener)
        addMouseMotionListener(mouseListener)
    }

    private fun mouseChange(point: Point) {
        val insets = getInsets()
        val x = insets.left
        val width = getWidth() - (insets.left + insets.right)
        val px = point.x - x
        var v = (px.toFloat()) / width.toFloat()

        v = max(0f, min(1f, v))
        valueChanged(v)
    }

    protected abstract fun valueChanged(v: Float)

    protected abstract val value: Float

    override fun paintComponent(g: Graphics) {
        val insets = getInsets()
        val x = insets.left
        val y = insets.top
        val width = getWidth() - (insets.left + insets.right)
        val height = getHeight() - (insets.top + insets.bottom)

        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val size = height + UIScale.scale(8)
        val selectionX = (x + width * this.value).toInt()
        val selectionY = (y + height / 2f).toInt()

        paintSelection(g2, selectionX, selectionY, size)

        super.paintComponent(g)
    }

    protected fun paintSelection(g2: Graphics2D, x: Int, y: Int, size: Int) {
        g2.translate((x - size / 2f).toDouble(), (y - size / 2f).toDouble())

        g2.color = UIManager.getColor("Component.borderColor")
        g2.fill(createShape(size.toFloat(), 0.6f, 0f))

        g2.color = Color.WHITE
        g2.fill(createShape(size.toFloat(), 0.6f, UIScale.scale(1f)))
    }
}
