package me.cdh.container

import me.cdh.CoroutineTimer
import me.cdh.palette.RainbowData
import java.awt.Color
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import javax.swing.JLabel
import kotlin.random.Random

class FlowLabel : JLabel() {
    var timeUpdateJob: CoroutineTimer? = null

    init {
        font = Font(GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames.random(), Font.BOLD, 50)
        foreground = WeakReference(RainbowData()).get()?.get(Random.nextInt(18)) ?: Color.WHITE
        horizontalAlignment = CENTER
        startNormalTimer()
        timeUpdateJob?.start()
    }

    fun startNormalTimer() {
        timeUpdateJob?.stop()
        timeUpdateJob = CoroutineTimer(1000L) {
            val now = LocalDateTime.now()
            text = String.format("%02d:%02d", now.hour, now.minute)
        }
        timeUpdateJob?.start()
    }
}