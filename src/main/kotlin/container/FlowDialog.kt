package me.cdh.container

import me.cdh.CoroutineTimer
import me.cdh.container.FlowContainer.remainingTime
import me.cdh.manager.TimeoutManager
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Insets
import javax.swing.*
import kotlin.text.iterator

class FlowDialog : JDialog(){
    val textField = JTextField()
    val toolTipLabel = JLabel("Format: 1h30m15s, 25m, 30s")
    val exitButton = JButton("Exit")
    val confirmButton = JButton("Confirm")
    val userInputCheck =
        "^(([1-23]\\d*h)?([1-59]\\d*m)?([1-59]\\d*s)?|([1-59]\\d*m)?([1-59]\\d*s)?|([1-59]\\d*s)?)$".toRegex()

    var countDownTimer: CoroutineTimer? = null

    init {
        toolTipLabel.foreground = Color.GRAY
        toolTipLabel.font = toolTipLabel.font.deriveFont(11f)
        toolTipLabel.alignmentX = CENTER_ALIGNMENT
        textField.preferredSize = Dimension(250, 35)
        textField.margin = Insets(5, 10, 5, 10)
        title = "Set a countdown"
        setSize(280, 150)
        setLocationRelativeTo(null)
        isModal = true

        confirmButton.preferredSize = Dimension(80, 30)
        exitButton.preferredSize = Dimension(80, 30)

        layout = MigLayout()
        add(textField, "span, grow")
        add(toolTipLabel, "wrap")
        add(confirmButton, "")
        add(exitButton, "gap unrelated")

        exitButton.addActionListener { dispose() }
        confirmButton.addActionListener {
            val inputText = textField.text.trim()
            // validate user input.
            if (inputText.isNotEmpty() && inputText.matches(userInputCheck)) {
                val milliseconds = timeStringToMilliseconds(inputText)
                dispose()
                startCountDown(milliseconds)
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Please enter valid characters!",
                    "Number Format Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    fun startCountDown(totalMillis: Long) {
        if (totalMillis == 0L) return
        countDownTimer?.stop()
        FlowContainer.label.timeUpdateJob?.pause()
        remainingTime = totalMillis
        updateDisplay()
        countDownTimer = CoroutineTimer(1000L) {
            remainingTime -= 1000
            updateDisplay()
            if (remainingTime <= 0) {
                countDownTimer?.stop()
                FlowContainer.label.timeUpdateJob?.start()
                // execute user timeout action
                TimeoutManager.executeSelectedAction()
            }
        }
        countDownTimer?.start()
    }

    private fun updateDisplay() {
        FlowContainer.label.text = msToTimeStr(remainingTime)
    }

    private fun timeStringToMilliseconds(timeStr: String): Long {
        val units = mapOf('h' to 60 * 60 * 1000, 'm' to 60 * 1000, 's' to 1000)
        var totalMilliSec = 0L
        var currNum = 0
        for (ch in timeStr) {
            if (ch.isDigit()) {
                currNum = currNum * 10 + ch.digitToInt()
            } else {
                if (ch in units) {
                    units[ch]?.let { totalMilliSec += currNum * it }
                    currNum = 0
                }
            }
        }
        if (currNum > 0) {
            totalMilliSec += currNum * 60 * 1000
        }
        return totalMilliSec
    }

    private fun msToTimeStr(timeMillis: Long): String {
        val totalSec = timeMillis / 1000
        val hours = totalSec / 3600
        val minutes = (totalSec % 3600) / 60
        val seconds = totalSec % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}