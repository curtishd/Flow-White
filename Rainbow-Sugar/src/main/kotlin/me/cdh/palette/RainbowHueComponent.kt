package me.cdh.palette

import com.formdev.flatlaf.util.ScaledEmptyBorder
import java.awt.Graphics

class RainbowHueComponent(private val colorPicker: RainbowColorPicker) : Slider() {
    init {
        install()
    }

    override fun install() {
        super.install()
        setBorder(ScaledEmptyBorder(5, 10, 5, 10))
    }

    override fun valueChanged(v: Float) {
        colorPicker.selectionModel?.setHue(v)
    }

    override val value: Float
        get() = colorPicker.selectionModel!!.getHue()

    override fun paintComponent(g: Graphics) {
        val insets = getInsets()
        val x = insets.left
        val y = insets.top
        val width = getWidth() - (insets.left + insets.right)
        val height = getHeight() - (insets.top + insets.bottom)

        // draw image
        val image = colorPicker.selectionModel?.getHueImage(width, height, height.toFloat())
        if (image != null) {
            g.drawImage(image, x, y, null)
        }
        super.paintComponent(g)
    }
}
