package me.cdh.palette

import com.formdev.flatlaf.util.UIScale
import java.awt.*
import java.awt.geom.Area
import java.awt.geom.RoundRectangle2D
import kotlin.math.min

class RainbowItemPainter : RainbowPaletteItemPainter {
    private val itemSize = Dimension(20, 20)

    override fun getItemSize(): Dimension {
        return itemSize
    }

    override fun getMaxRow(): Int {
        return 2
    }

    override fun getItemGap(): Int {
        return 10
    }

    override fun getItemBorderSize(): Int {
        return 3
    }

    private val arc: Float
        get() = 8f

    override fun paintItem(
        g: Graphics?,
        color: Color?,
        width: Int,
        height: Int,
        isSelected: Boolean,
        hasFocus: Boolean
    ) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = color
        val arc = min(UIScale.scale(this.arc), min(width, height).toFloat())
        g2.fill(RoundRectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat(), arc, arc))

        val border = UIScale.scale(getItemBorderSize())
        if (border > 0) {
            g2.translate(-border, -border)
            paintItemBorder(g2, width + border * 2, height + border * 2, hasFocus)
        }
    }

    private fun paintItemBorder(g2: Graphics2D, width: Int, height: Int, hasFocus: Boolean) {
        if (!(hasFocus)) return
        g2.composite = AlphaComposite.SrcOver.derive(0.35f)
        val border = UIScale.scale(getItemBorderSize())
        var arc = UIScale.scale(this.arc + getItemBorderSize() * 2)
        arc = min(arc, min(width, height).toFloat())
        val area = Area(RoundRectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat(), arc, arc))
        val inWidth = width - border * 2
        val inHeight = height - border * 2
        val inArc = min(UIScale.scale(this.arc), min(inWidth, inHeight).toFloat())

        area.subtract(
            Area(
                RoundRectangle2D.Float(
                    border.toFloat(),
                    border.toFloat(),
                    inWidth.toFloat(),
                    inHeight.toFloat(),
                    inArc,
                    inArc
                )
            )
        )
        g2.fill(area)
    }
}
