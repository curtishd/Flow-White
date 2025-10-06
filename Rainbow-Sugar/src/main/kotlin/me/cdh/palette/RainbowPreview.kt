package me.cdh.palette

import com.formdev.flatlaf.util.UIScale
import java.awt.*
import java.awt.geom.Area
import java.awt.geom.RoundRectangle2D
import javax.swing.JComponent
import javax.swing.UIManager

class RainbowPreview : JComponent() {

    fun setColor(color: Color?) {
        this.color = color
        repaint()
    }

    private var color: Color? = null

    override fun paintComponent(g: Graphics) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val width = getWidth()
        val height = getHeight()

        val arc = UIScale.scale(10f)
        val border = UIScale.scale(1.5f)
        g2.color = UIManager.getColor("Component.borderColor")
        g2.composite = AlphaComposite.SrcOver.derive(0.7f)
        val area = Area(RoundRectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat(), arc, arc))
        area.subtract(
            Area(
                RoundRectangle2D.Float(
                    border,
                    border,
                    width - border * 2,
                    height - border * 2,
                    arc - border,
                    arc - border
                )
            )
        )
        g2.fill(area)

        if (color != null) {
            g2.color = color
            g2.composite = AlphaComposite.SrcOver
            g2.fill(
                RoundRectangle2D.Float(
                    border,
                    border,
                    width - border * 2,
                    height - border * 2,
                    arc - border,
                    arc - border
                )
            )
        }
        g2.dispose()
        super.paintComponent(g)
    }
}
