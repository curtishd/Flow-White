package me.cdh.palette

import java.awt.Shape
import java.awt.geom.Area
import java.awt.geom.Ellipse2D

fun createShape(size: Float, border: Float, margin: Float): Shape {
    val area = Area(Ellipse2D.Float(margin, margin, size - margin * 2f, size - margin * 2f))
    val borderSize = (size / 2f * border)
    val centerSize = (size - borderSize * 2f) + margin * 2f
    area.subtract(Area(Ellipse2D.Float(borderSize - margin, borderSize - margin, centerSize, centerSize)))
    return area
}