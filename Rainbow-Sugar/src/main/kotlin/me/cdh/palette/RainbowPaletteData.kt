package me.cdh.palette

import java.awt.Color

interface RainbowPaletteData {
    fun size(): Int

    fun get(index: Int): Color?
}