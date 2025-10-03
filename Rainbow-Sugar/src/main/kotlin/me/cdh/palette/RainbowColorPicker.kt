package me.cdh.palette

import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Component
import javax.swing.JOptionPane
import javax.swing.JPanel

class RainbowColorPicker(initialColor: Color?) : JPanel(), RainbowChangedListener {
    var selectionModel: RainbowPickerSelectionModel? = null
        set(selectionModel) {
            requireNotNull(selectionModel) { "color selectionModel can't be null" }
            if (field != selectionModel) {
                val old = field
                old?.removeChangeListener(this)
                field = selectionModel
                field!!.addChangeListener(this)
            }
        }
    private var colorComponent: RainbowComponent? = null
    private var colorHueComponent: RainbowHueComponent? = null
    private var colorAlphaComponent: RainbowAlphaComponent? = null
    private var colorPreview: RainbowPreview? = null
    private var colorField: RainbowField? = null
    private var colorPalette: RainbowPaletteComponent? = null

    init {
        init(RainbowPickerSelectionModel(), initialColor)
    }

    private fun init(selectionModel: RainbowPickerSelectionModel, initialColor: Color?) {
        setLayout(MigLayout("wrap,fillx,gap 0,insets 0 0 5 0", "[fill,280]"))

        this.selectionModel = selectionModel
        colorComponent = RainbowComponent(this)
        colorHueComponent = RainbowHueComponent(this)
        colorAlphaComponent = RainbowAlphaComponent(this)
        colorField = RainbowField(this)

        val panel = JPanel(MigLayout("wrap 2,fillx,insets 0,gap 3", "7[grow 0,fill][fill]"))

        panel.setOpaque(false)
        colorComponent?.let { add(it, "height 50:180:") }
        panel.add(createLeftComponent(), "span 1 2")
        colorHueComponent?.let { panel.add(it, "height 20!") }
        colorAlphaComponent?.let { panel.add(it, "height 20!") }
        add(panel)

        add(colorField)
        add(createColorPalette())
        selectionModel.setSelectedColor(initialColor)

        if (colorField != null) {
            colorField!!.colorChanged(selectionModel.getSelectedColor())
        }
    }

    private fun createLeftComponent(): Component {
        val panel = JPanel(MigLayout("insets 3"))
        panel.setOpaque(false)

        colorPreview = RainbowPreview()

        colorPreview?.let { panel.add(it, "width 33,height 33") }

        return panel
    }

    private fun createColorPalette(): Component? {
        if (colorPalette == null) {
            colorPalette = RainbowPaletteComponent(RainbowData(), RainbowItemPainter())
            colorPalette!!.addChangeListener {
                val color = colorPalette!!.getColorAt(colorPalette!!.getSelectedIndex())
                if (color != null) {
                    this.selectionModel!!.setSelectedColor(color)
                }
            }
        }
        return colorPalette
    }

    val selectedColor: Color?
        get() = this.selectionModel!!.getSelectedColor()


    override fun colorChanged(color: Color, event: RainbowChangeEvent) {
        if (colorComponent != null) {
            if (colorComponent!!.isNotifyRepaint) {
                if (!event.hueChanged) {
                    // selected color invoked
                    // so coverts color to selected point
                    colorComponent!!.changeSelectedPoint(color)
                }
                colorComponent!!.repaint()
            }
        }
        colorHueComponent!!.repaint()
        colorAlphaComponent!!.repaint()
        colorPreview!!.setColor(color)
        colorField!!.colorChanged(color)
        fireColorChanged(event)
    }

    fun fireColorChanged(event: RainbowChangeEvent) {
        val listeners = listenerList.getListenerList()
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === RainbowChangedListener::class.java) {
                (listeners[i + 1] as RainbowChangedListener).colorChanged(this.selectedColor!!, event)
            }
            i -= 2
        }
    }

    companion object {
        fun showDialog(component: Component?, title: String?, initialColor: Color?): Color? {
            val colorPicker = RainbowColorPicker(initialColor ?: Color.WHITE)
            return showDialog(component, title, colorPicker)
        }

        fun showDialog(component: Component?, title: String?, colorPicker: RainbowColorPicker): Color? {
            val option = JOptionPane.showConfirmDialog(
                component, colorPicker, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            )
            if (option == JOptionPane.OK_OPTION) {
                return colorPicker.selectedColor
            }
            return null
        }
    }
}
