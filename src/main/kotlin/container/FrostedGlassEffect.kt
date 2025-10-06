package me.cdh.container

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.util.random.RandomGenerator

class FrostedGlassEffect {
    var enabled = true
    fun enable() {
        enabled = true
        FlowContainer.repaint()
    }

    fun disable() {
        enabled = false
        FlowContainer.repaint()

    }

    fun paint(g: Graphics2D) {
        if (!enabled) return
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        createFrostedGlassEffect(g)
    }

    private fun createFrostedGlassEffect(g2d: Graphics2D) {
        // 创建圆角矩形区域
        val arc = 20f // 圆角半径
        val area =
            RoundRectangle2D.Float(0f, 0f, FlowContainer.width.toFloat(), FlowContainer.height.toFloat(), arc, arc)

        // 设置绘制区域
        g2d.clip = area

        // 绘制半透明背景（磨砂玻璃基础色）
        val baseColor = Color(240, 240, 245, (255 * 0.15f).toInt())
        g2d.color = baseColor
        g2d.fill(area)

        // 添加磨砂玻璃的纹理效果 - 绘制一些微小的噪点
        val random = RandomGenerator.getDefault()
        (0..<50).forEach { _ ->
            val x = random.nextInt(FlowContainer.width)
            val y = random.nextInt(FlowContainer.height)
            val size = random.nextInt(3) + 1
            val alpha = random.nextInt(30) + 10

            g2d.color = Color(255, 255, 255, alpha)
            g2d.fillOval(x, y, size, size)
        }

        // 添加边框效果
        g2d.color = Color(200, 200, 210, 100)
        g2d.stroke = BasicStroke(1.5f)
        g2d.draw(area)

        // 添加内发光效果
        g2d.color = Color(255, 255, 255, 80)
        g2d.stroke = BasicStroke(1f)
        g2d.draw(
            RoundRectangle2D.Float(
                1f,
                1f,
                (FlowContainer.width - 2).toFloat(),
                (FlowContainer.height - 2).toFloat(),
                arc - 1,
                arc - 1
            )
        )
        g2d.clip = null
    }
}