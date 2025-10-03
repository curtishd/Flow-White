package me.cdh

import me.cdh.FlowContainer.remainingTime
import java.awt.*
import javax.swing.*

object FlowDialog {
    val dialog = JDialog()
    val textField = JTextField()
    val toolTipLabel = JLabel("Format: 1h30m15s, 25m, 30s")
    val exitButton = JButton("Exit")
    val confirmButton = JButton("Confirm")

    var countDownTimer: CoroutineTimer? = null

    init {
        textField.preferredSize = Dimension(250, 35)
        textField.margin = Insets(5, 10, 5, 10)
        dialog.title = "Set a countdown"
        dialog.setSize(320, 180)
        dialog.setLocationRelativeTo(null)
        dialog.isModal = true

        // Set union size
        confirmButton.preferredSize = Dimension(80, 30)
        exitButton.preferredSize = Dimension(80, 30)

        val vLayout = Box.createVerticalBox()
        vLayout.border = BorderFactory.createEmptyBorder(20, 20, 20, 20)

        // text field
        val textFieldPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        textFieldPanel.add(textField)
        vLayout.add(textFieldPanel)

        // tip content
        vLayout.add(Box.createVerticalStrut(8))
        toolTipLabel.foreground = Color.GRAY
        toolTipLabel.font = toolTipLabel.font.deriveFont(11f)
        toolTipLabel.alignmentX = Component.CENTER_ALIGNMENT
        vLayout.add(toolTipLabel)

        // Button area
        vLayout.add(Box.createVerticalStrut(20))
        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 20, 0))
        buttonPanel.add(confirmButton)
        buttonPanel.add(exitButton)
        vLayout.add(buttonPanel)

        dialog.add(vLayout)

        exitButton.addActionListener { dialog.dispose() }
        confirmButton.addActionListener {
            val inputText = textField.text.trim()
            if (inputText.isNotEmpty()) {
                val milliseconds = timeStringToMilliseconds(inputText)
                startCountDown(milliseconds)
            }
            dialog.dispose()
        }
    }

    fun startCountDown(totalMillis: Int) {
        if (totalMillis == 0) return
        countDownTimer?.stop()
        FlowLabel.timeUpdateJob?.pause()
        remainingTime = totalMillis
        updateDisplay()
        countDownTimer = CoroutineTimer(1000L) {
            remainingTime -= 1000
            updateDisplay()
            if (remainingTime <= 0) {
                countDownTimer?.stop()
                FlowLabel.timeUpdateJob?.start()
                FlowContainer.showTimeUpNotification()
            }
        }
        countDownTimer?.start()
    }

    fun updateDisplay() {
        FlowLabel.text = msToTimeStr(remainingTime)
    }

    fun timeStringToMilliseconds(timeStr: String): Int {
        val units = mapOf('h' to 60 * 60 * 1000, 'm' to 60 * 1000, 's' to 1000)
        var totalMilliSec = 0
        var currNum = 0
        for (ch in timeStr) {
            if (ch.isDigit()) {
                currNum = currNum * 10 + ch.digitToInt()
            } else {
                if (ch in units) {
                    units[ch]?.let { totalMilliSec += currNum * it }
                    currNum = 0
                } else {
                    // Invalid input
                    JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter valid characters!",
                        "Number Format Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                    return 0
                }
            }
        }
        if (currNum > 0) {
            totalMilliSec += currNum * 60 * 1000
        }
        return totalMilliSec
    }

    fun msToTimeStr(timeMillis: Int): String {
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