package me.cdh.palette

import java.util.EventObject

class RainbowChangeEvent(source: Any, val hueChanged: Boolean): EventObject(source)