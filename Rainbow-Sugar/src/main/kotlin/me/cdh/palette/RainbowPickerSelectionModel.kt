package me.cdh.palette

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.GradientPaint
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import javax.swing.event.EventListenerList

class RainbowPickerSelectionModel {
    private val listenerList = EventListenerList()
    private var hueImage: BufferedImage? = null
    private var colorImage: BufferedImage? = null
    private var selectedColor: Color? = null
    private var hue = 0f

    private var oldHue = -1f
    private var oldHueArc = 0f
    private var oldColorArc = 0f

    fun getHueImage(width: Int, height: Int, arc: Float): BufferedImage? {
        createHueColor(width, height, arc)
        return hueImage
    }

    private data class ImageCacheKey(val width: Int, val height: Int, val arc: Float, val hue: Float)

    private var colorImageCache = mutableMapOf<ImageCacheKey, BufferedImage>()

    fun fetchColorImage(width: Int, height: Int, arc: Float): BufferedImage {
        val key = ImageCacheKey(width, height, arc, hue)
        return colorImageCache.getOrPut(key) {
            createColorImage(width, height, arc)!!
        }
    }

    fun getSelectedColor(): Color? {
        return selectedColor
    }

    fun setSelectedColor(selectedColor: Color?) {
        setSelectedColor(selectedColor, true)
    }

    fun setSelectedColor(selectedColor: Color?, changeHue: Boolean) {
        var selectedColor = selectedColor
        if (selectedColor == null) {
            selectedColor = this.defaultColor
        }
        if (this.selectedColor != selectedColor) {
            var hueChanged = false
            if (changeHue) {
                this.hue = toHueColor(selectedColor)
            } else {
                // check is only alpha changed
                if (isAlphaChangedOnly(this.selectedColor, selectedColor)) {
                    hueChanged = true
                }
            }
            this.selectedColor = selectedColor
            fireColorChanged(RainbowChangeEvent(this, hueChanged))
        }
    }

    private fun isAlphaChangedOnly(color1: Color?, color2: Color?): Boolean {
        if (color1 == null || color2 == null) {
            return false
        }

        val r1 = color1.red
        val g1 = color1.green
        val b1 = color1.blue
        val a1 = color1.alpha

        val r2 = color2.red
        val g2 = color2.green
        val b2 = color2.blue
        val a2 = color2.alpha
        return r1 == r2 && g1 == g2 && b1 == b2 && a1 != a2
    }

    fun getHue(): Float {
        return hue
    }

    fun setHue(hue: Float) {
        var hue = hue
        if (hue < 0f) {
            hue = 0f
        } else if (hue > 1f) {
            hue = 1f
        }
        if (this.hue != hue) {
            this.hue = hue
            if (selectedColor != null) {
                val hsb = Color.RGBtoHSB(
                    selectedColor!!.red,
                    selectedColor!!.green,
                    selectedColor!!.blue,
                    null
                )
                val oldColor = selectedColor
                val alpha = oldColor!!.alpha
                selectedColor = Color.getHSBColor(hue, hsb[1], hsb[2])
                if (alpha != 255) {
                    selectedColor =
                        Color(selectedColor!!.red, selectedColor!!.green, selectedColor!!.blue, alpha)
                }
            }
            fireColorChanged(RainbowChangeEvent(this, true))
        }
    }

    private val defaultColor: Color?
        get() = Color.WHITE

    private fun toHueColor(color: Color?): Float {
        if (color == null) {
            return 0f
        }
        return Color.RGBtoHSB(color.red, color.green, color.blue, null)[0]
    }

    private fun createHueColor(width: Int, height: Int, arc: Float) {
        if (width <= 0 || height <= 0) {
            return
        }
        if (hueImage == null || (hueImage!!.width != width || hueImage!!.height != height || oldHueArc != arc)) {
            hueImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val g2 = hueImage!!.createGraphics()
            if (arc > 0) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.fill(RoundRectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat(), arc, arc))
                g2.composite = AlphaComposite.SrcIn
            }
            for (i in 0..<width) {
                val v = i.toFloat() / width.toFloat()
                g2.color = Color.getHSBColor(v, 1f, 1f)
                g2.drawLine(i, 0, i, height)
            }
            g2.dispose()
            oldHueArc = arc
        }
    }

    private fun createColorImage(width: Int, height: Int, arc: Float): BufferedImage? {
        if (width <= 0 || height <= 0) return null
        if (colorImage == null || (colorImage!!.width != width || colorImage!!.height != height || oldColorArc != arc) || oldHue != hue) {
            colorImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val g2 = colorImage!!.createGraphics()
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            val primary = GradientPaint(0f, 0f, Color.WHITE, width.toFloat(), 0f, Color.getHSBColor(hue, 1f, 1f))
            val shade = GradientPaint(0f, 0f, Color(0, 0, 0, 0), 0f, height.toFloat(), Color(0, 0, 0, 255))
            g2.paint = primary
            g2.fill(RoundRectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat(), arc, arc))
            g2.paint = shade
            g2.fill(RoundRectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat(), arc, arc))
            g2.dispose()
            oldHue = hue
            oldColorArc = arc
        }
        return colorImage
    }

    fun addChangeListener(listener: RainbowChangedListener?) {
        listenerList.add(RainbowChangedListener::class.java, listener)
    }

    fun removeChangeListener(listener: RainbowChangedListener?) {
        listenerList.remove(RainbowChangedListener::class.java, listener)
    }

    fun fireColorChanged(event: RainbowChangeEvent) {
        val listeners = listenerList.getListenerList()
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === RainbowChangedListener::class.java) {
                (listeners[i + 1] as RainbowChangedListener).colorChanged(getSelectedColor()!!, event)
            }
            i -= 2
        }
    }
}