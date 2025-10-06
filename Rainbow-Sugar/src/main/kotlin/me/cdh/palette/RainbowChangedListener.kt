package me.cdh.palette

import java.awt.Color
import java.util.EventListener

interface RainbowChangedListener : EventListener {
    fun colorChanged(color: Color, event:RainbowChangeEvent)
}