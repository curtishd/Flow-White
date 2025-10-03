package me.cdh

import java.awt.Font
import java.awt.GraphicsEnvironment
import javax.swing.ButtonGroup
import javax.swing.JMenu
import javax.swing.JRadioButtonMenuItem

object FontMenuManager {
    private var isInitialized = false
    private val fontButtons = mutableListOf<JRadioButtonMenuItem>()
    private lateinit var buttonGroup: ButtonGroup
    fun initializeFontMenu(menu: JMenu) {
        if (isInitialized) return
        buttonGroup = ButtonGroup()
        val currFont = FlowLabel.font
        for (fontName in GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames) {
            val fontButton = JRadioButtonMenuItem(fontName).apply {
                isSelected = fontName.equals(currFont.name, ignoreCase = true)
                addActionListener {
                    FlowLabel.font = Font(fontName, Font.BOLD, 50)
                    updateSelection(fontName)
                }
            }
            menu.add(fontButton)
            buttonGroup.add(fontButton)
            fontButtons.add(fontButton)
        }
        isInitialized = true
    }

    private fun updateSelection(selectedFontName: String) {
        for (btn in fontButtons) {
            btn.isSelected = btn.text == selectedFontName
        }
    }
}