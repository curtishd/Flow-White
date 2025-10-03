package me.cdh.palette

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics

interface RainbowPaletteItemPainter {
    fun getItemSize(): Dimension?

    fun getMaxRow(): Int

    fun getItemGap(): Int

    fun getItemBorderSize(): Int

    fun paintItem(g: Graphics?, color: Color?, width: Int, height: Int, isSelected: Boolean, hasFocus: Boolean)
}