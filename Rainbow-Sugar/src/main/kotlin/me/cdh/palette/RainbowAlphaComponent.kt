package me.cdh.palette

import com.formdev.flatlaf.util.ScaledEmptyBorder
import java.awt.*
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import kotlin.math.ceil

class RainbowAlphaComponent(private val colorPicker: RainbowColorPicker) : Slider() {
    private var image: BufferedImage? = null

    init {
        install()
    }

    override fun install() {
        super.install()
        setBorder(ScaledEmptyBorder(5, 10, 5, 10))
    }

    override fun valueChanged(v: Float) {
        val color = colorPicker.selectionModel?.getSelectedColor()
        if (color != null) {
            colorPicker.selectionModel!!
                .setSelectedColor(Color(color.red, color.green, color.blue, (v * 255f).toInt()), false)
        }
    }

    override val value: Float
        get() {
            val color = colorPicker.selectionModel?.getSelectedColor() ?: return 1f
            return color.alpha / 255f
        }

    override fun paintComponent(g: Graphics) {
        val insets = getInsets()
        val x = insets.left
        val y = insets.top
        val width = getWidth() - (insets.left + insets.right)
        val height = getHeight() - (insets.top + insets.bottom)

        val img = createTransparentImage(width, height)
        if (img != null) {
            val g2 = g.create() as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.drawImage(img, x, y, null)
            g2.paint = GradientPaint(
                x.toFloat(),
                y.toFloat(),
                Color(255, 255, 255, 0),
                (x + width).toFloat(),
                y.toFloat(),
                Color.getHSBColor(colorPicker.selectionModel!!.getHue(), 1f, 1f)
            )
            g2.fill(
                RoundRectangle2D.Float(
                    x.toFloat(),
                    y.toFloat(),
                    width.toFloat(),
                    height.toFloat(),
                    height.toFloat(),
                    height.toFloat()
                )
            )
            g2.dispose()
        }
        super.paintComponent(g)
    }

    private fun createTransparentImage(width: Int, height: Int): BufferedImage? {
        if (image == null || image!!.width != width || image!!.height != height) {
            val row = 2
            val size = height / row.toFloat()
            if (size <= 0) {
                return null
            }
            image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val g2 = image!!.createGraphics()
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.fill(
                RoundRectangle2D.Float(
                    0f,
                    0f,
                    width.toFloat(),
                    height.toFloat(),
                    height.toFloat(),
                    height.toFloat()
                )
            )
            g2.composite = AlphaComposite.SrcIn.derive(0.5f)

            // draw transparent background
            val count = ceil((width / size).toDouble()).toInt()
            val color1 = getBackground()
            val color2 = Color.GRAY
            for (i in 0..<row) {
                for (j in 0..<count) {
                    if ((i + j) % 2 == 0) {
                        g2.color = color1
                    } else {
                        g2.color = color2
                    }
                    g2.fill(Rectangle2D.Float(j * size, size * i, size, size))
                }
            }
        }
        return image
    }
}
