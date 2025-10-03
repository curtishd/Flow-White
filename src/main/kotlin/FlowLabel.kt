package me.cdh

import java.awt.Color
import java.awt.Font
import java.time.LocalDateTime
import javax.swing.JLabel

object FlowLabel : JLabel() {
    var timeUpdateJob: CoroutineTimer? = null

    init {
        font = Font("Ink Free", Font.BOLD, 50)
        foreground = Color.PINK
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

    @Suppress
    private fun readResolve(): Any = FlowLabel
}
