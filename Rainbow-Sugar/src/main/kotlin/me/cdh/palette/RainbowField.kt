package me.cdh.palette

import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.text.ParseException
import java.util.*
import javax.swing.*
import javax.swing.text.DefaultFormatter
import javax.swing.text.NumberFormatter

class RainbowField(private val colorPicker: RainbowColorPicker) : JComponent(), PropertyChangeListener {
    private val txtRed: JFormattedTextField
    private val txtGreen: JFormattedTextField
    private val txtBlue: JFormattedTextField
    private val txtAlpha: JFormattedTextField
    private val txtHex: JFormattedTextField

    private var red = 0
    private var green = 0
    private var blue = 0
    private var alpha = 0

    private var hex: String? = null

    init {
        setLayout(
            MigLayout(
                "insets n 10 5 10,wrap 5,gapy 5,fillx",
                "[center,grow 0][center,grow 0][center,grow 0][center,grow 0][fill]"
            )
        )

        txtRed = createTextField()
        txtGreen = createTextField()
        txtBlue = createTextField()
        txtAlpha = createTextField()
        txtHex = createHexTextField()

        add(JLabel("R"))
        add(JLabel("G"))
        add(JLabel("B"))
        add(JLabel("A"))
        add(JLabel("Hex", SwingConstants.CENTER))

        add(txtRed)
        add(txtGreen)
        add(txtBlue)
        add(txtAlpha)
        add(txtHex)
    }

    private fun createTextField(): JFormattedTextField {
        val txt = JFormattedTextField(createFormatter())
        txt.setHorizontalAlignment(SwingConstants.CENTER)
        txt.setColumns(2)
        txt.addPropertyChangeListener("value", this)
        txt.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                if (txt.value == null) {
                    txt.setValue(0)
                }
            }

            override fun focusGained(e: FocusEvent?) {
                SwingUtilities.invokeLater { txt.selectAll() }
            }
        })
        return txt
    }

    private fun createHexTextField(): JFormattedTextField {
        val txt = JFormattedTextField(createHexFormatter())
        txt.addPropertyChangeListener("value", this)
        txt.setHorizontalAlignment(SwingConstants.CENTER)
        txt.addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent?) {
                SwingUtilities.invokeLater { txt.selectAll() }
            }
        })
        return txt
    }

    private fun createFormatter(): NumberFormatter {
        val formatter = object : NumberFormatter() {
            @Throws(ParseException::class)
            override fun stringToValue(text: String?): Any? {
                if (text == null || text.trim { it <= ' ' }.isEmpty()) {
                    return null
                }
                return super.stringToValue(text)
            }
        }
        formatter.valueClass = Int::class.java
        formatter.setMinimum(0)
        formatter.setMaximum(255)
        formatter.commitsOnValidEdit = true
        return formatter
    }

    private fun createHexFormatter(): DefaultFormatter {
        val formatter: DefaultFormatter = object : DefaultFormatter() {
            @Throws(ParseException::class)
            override fun stringToValue(string: String?): Any {
                var string = string
                if (string != null) {
                    string = string.trim { it <= ' ' }
                    string = if (string.startsWith("#")) string.substring(1) else string
                    if (string.matches("^[0-9a-fA-F]{6,8}$".toRegex())) {
                        return string.uppercase(Locale.getDefault())
                    }
                }
                throw ParseException("Invalid hex color", 0)
            }
        }
        formatter.commitsOnValidEdit = true

        return formatter
    }

    fun colorChanged(color: Color?) {
        if (color == null) {
            txtRed.setValue(0)
            txtGreen.setValue(0)
            txtBlue.setValue(0)
            txtAlpha.setValue(0)
            txtHex.setValue(null)
        } else {
            val red = color.red
            val green = color.green
            val blue = color.blue
            val alpha = color.alpha
            this.red = red
            txtRed.setValue(red)

            this.green = green
            txtGreen.setValue(green)

            this.blue = blue
            txtBlue.setValue(blue)

            this.alpha = alpha
            txtAlpha.setValue(alpha)
            this.hex = colorToHex(color)
            txtHex.setValue(hex)
        }
    }

    private fun colorToHex(color: Color): String {
        return String.format("%02X%02X%02X%02X", color.red, color.green, color.blue, color.alpha)
    }

    private fun decodeRGBA(hex: String): Color {
        var hex = hex

        hex = hex.trim { it <= ' ' }
        if (hex.startsWith("#")) {
            hex = hex.substring(1)
        }

        // Validate hex length and characters
        require(hex.matches("^[0-9a-fA-F]{6,8}$".toRegex())) { "Invalid RGBA color format" }

        // Normalize hex string to 8 digits by handling 6 or 7 digit cases
        if (hex.length == 6) {
            hex += "FF" // full opacity
        } else if (hex.length == 7) {
            // last digit is half alpha, pad with 'F'
            hex = hex.take(6) + hex[6] + "F"
        }

        // Parse the 8-digit RGBA hex into int
        val rgba = hex.toLong(16).toInt()

        return Color(
            (rgba shr 24) and 0xFF,  // Red
            (rgba shr 16) and 0xFF,  // Green
            (rgba shr 8) and 0xFF,  // Blue
            rgba and 0xFF // Alpha
        )
    }

    override fun propertyChange(evt: PropertyChangeEvent) {
        onChanged(evt.getSource())
    }

    private fun onChanged(source: Any?) {
        if (source === txtHex) {
            val colorHex = if (txtHex.value == null) null else txtHex.value.toString()
            if (this.hex != colorHex) {
                if (colorHex != null) {
                    colorPicker.selectionModel?.setSelectedColor(decodeRGBA(colorHex))
                }
                this.hex = colorHex
            }
        } else {
            val red = if (txtRed.value == null) 0 else txtRed.value.toString().toInt()
            val green = if (txtGreen.value == null) 0 else txtGreen.value.toString().toInt()
            val blue = if (txtBlue.value == null) 0 else txtBlue.value.toString().toInt()
            val alpha = if (txtAlpha.value == null) 0 else txtAlpha.value.toString().toInt()
            if (this.red != red || this.green != green || this.blue != blue || this.alpha != alpha) {
                this.red = red
                this.green = green
                this.blue = blue
                this.alpha = alpha
                colorPicker.selectionModel?.setSelectedColor(Color(red, green, blue, alpha))
            }
        }
    }
}
