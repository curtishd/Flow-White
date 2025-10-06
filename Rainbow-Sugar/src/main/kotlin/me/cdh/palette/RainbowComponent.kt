package me.cdh.palette

import com.formdev.flatlaf.util.ScaledEmptyBorder
import com.formdev.flatlaf.util.UIScale
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Ellipse2D
import java.awt.geom.Point2D
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.UIManager

class RainbowComponent(private val colorPicker: RainbowColorPicker) : JComponent() {
    private var selectedPoint: Point2D.Float? = null
    var isNotifyRepaint: Boolean = true
        private set

    init {
        setBorder(ScaledEmptyBorder(10, 10, 10, 10))
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

        if (selectedPoint == null && (colorPicker.selectionModel != null && colorPicker.selectionModel!!
                .getSelectedColor() != null)
        ) {
            selectedPoint = colorToPoint(colorPicker.selectionModel!!.getSelectedColor()!!)
        }
    }

    fun changeSelectedPoint(color: Color?) {
        selectedPoint = if (color == null) {
            null
        } else {
            colorToPoint(color)
        }
    }

    private fun mouseChange(point: Point) {
        val insets = getInsets()
        point.x -= insets.left
        point.y -= insets.top
        val width = getWidth() - (insets.left + insets.right)
        val height = getHeight() - (insets.top + insets.bottom)
        selectedPoint = toPoint(point, width, height)
        var color = pointToColor(selectedPoint!!, colorPicker.selectionModel!!.getHue())
        val oldColor = colorPicker.selectionModel?.getSelectedColor()
        if (oldColor != null) {
            val alpha = oldColor.alpha
            if (alpha != 255) {
                color = Color(color.red, color.green, color.blue, alpha)
            }
        }
        try {
            this.isNotifyRepaint = false
            colorPicker.selectionModel?.setSelectedColor(color, false)
        } finally {
            repaint()
            this.isNotifyRepaint = true
        }
    }

    override fun paintComponent(g: Graphics) {
        val insets = getInsets()
        val x = insets.left
        val y = insets.top
        val width = getWidth() - (insets.left + insets.right)
        val height = getHeight() - (insets.top + insets.bottom)
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val image = colorPicker.selectionModel?.fetchColorImage(width, height, UIScale.scale(10f))
        if (image != null) {
            g2.drawImage(image, x, y, null)
        }

        paintSelection(g2, x, y, width, height)
        g2.dispose()
        super.paintComponent(g)
    }

    private fun paintSelection(g2: Graphics2D, x: Int, y: Int, width: Int, height: Int) {
        if (selectedPoint == null) {
            return
        }

        val size = UIScale.scale(18f)
        val lx = selectedPoint!!.x * width
        val ly = selectedPoint!!.y * height
        g2.translate((x + lx - size / 2f).toDouble(), (y + ly - size / 2f).toDouble())

        g2.color = UIManager.getColor("Component.borderColor")
        g2.fill(Ellipse2D.Float(0f, 0f, size, size))

        g2.color = Color.WHITE
        val border = UIScale.scale(1f)
        g2.fill(Ellipse2D.Float(border, border, size - border * 2, size - border * 2))

        g2.color = Color(colorPicker.selectionModel?.getSelectedColor()!!.rgb)
        val borderIn = size * 0.25f
        g2.fill(Ellipse2D.Float(borderIn, borderIn, size - borderIn * 2, size - borderIn * 2))
    }

    private fun colorToPoint(color: Color): Point2D.Float {
        val rgb = color.rgb
        val r = (rgb and 16) and 0xff
        val g = (rgb shr 8) and 0xff
        val b = rgb and 0xff
        val max = maxOf(r, g, b)
        val min = maxOf(r, g, b)
        val saturation = if (max == 0) 0f else (max - min) / max.toFloat()
        val brightness = max / 255f
        return Point2D.Float(saturation, 1f - brightness)
    }

    private fun pointToColor(point: Point2D.Float, hue: Float): Color {
        val saturation = point.x
        val brightness = 1f - point.y
        return Color.getHSBColor(hue, saturation, brightness)
    }

    private fun toPoint(point: Point, width: Int, height: Int): Point2D.Float {
        val x = clamp(point.x / width.toFloat())
        val y = clamp(point.y / height.toFloat())
        return Point2D.Float(x, y)
    }

    private fun clamp(value: Float): Float {
        return Math.clamp(value, 0f, 1f)
    }
}
