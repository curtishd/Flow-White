package me.cdh.container

import me.cdh.manager.SystemTrayManager
import java.awt.Desktop
import java.net.URI
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

sealed interface TimeoutAction {
    val name: String
    fun execute()
    fun configure(): Boolean


    class OpenApplicationAction : TimeoutAction {
        var applicationPath: String? = null
        override val name: String get() = "Open Application"

        override fun execute() {
            applicationPath?.let { path ->
                try {
                    Runtime.getRuntime().exec(arrayOf(path))
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Failed to open application: ${e.message}",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            } ?: run {
                JOptionPane.showMessageDialog(
                    null,
                    "No application configured",
                    "Configuration Error",
                    JOptionPane.WARNING_MESSAGE
                )
            }
        }

        override fun configure(): Boolean {
            val fileChooser = JFileChooser()
            fileChooser.isAcceptAllFileFilterUsed = false
            fileChooser.dialogTitle = "Select Application"
            fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
            val os = System.getProperty("os.name").lowercase()
            val executableFilter = when {
                os.contains("win") -> FileNameExtensionFilter(
                    "Windows Executable Files (*.exe, *.cmd, *.bat, *.msi, *.com, *.ps1)",
                    "exe",
                    "cmd",
                    "bat",
                    "msi",
                    "com",
                    "ps1"
                )

                os.contains("linux") || os.contains("unix") -> FileNameExtensionFilter(
                    "Linux Executable Files (*.sh, *.bin, *.run)",
                    "sh",
                    "bin",
                    "run"
                )

                os.contains("mac") -> FileNameExtensionFilter(
                    "macOS Applications (*.app, *.dmg, *.pkg)",
                    "app",
                    "dmg",
                    "pkg"
                )

                else -> FileNameExtensionFilter("All Files", "*")
            }
            fileChooser.fileFilter = executableFilter
            return if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                applicationPath = fileChooser.selectedFile.absolutePath
                true
            } else false
        }

    }

    class OpenWebPageAction : TimeoutAction {
         var url = ""
        override val name: String get() = "Open Web Page"

        override fun execute() {
            if (url.isEmpty()) {
                JOptionPane.showMessageDialog(
                    null,
                    "No URL configured for web page action",
                    "Configuration Error",
                    JOptionPane.WARNING_MESSAGE
                )
                return
            }
            try {
                Desktop.getDesktop().browse(URI(url))
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to open URL: ${e.message}",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }

        override fun configure(): Boolean {
            val inputUrl = JOptionPane.showInputDialog(
                null,
                "Enter URL:",
                "Configure Web Page Action",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                url.ifEmpty { "https://www.example.com" }
            ) as? String

            return inputUrl?.let {
                url = if (!it.startsWith("http")) "https://$it" else it
                true
            } ?: false
        }
    }

    class ShutdownAction : TimeoutAction {
        override val name: String get() = "Shutdown Computer"

        override fun execute() {
            try {
                val result = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to shutdown the computer?",
                    "Shutdown Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                )
                if (result == JOptionPane.YES_OPTION) {
                    val os = System.getProperty("os.name").lowercase()
                    val command = when {
                        os.contains("win") -> arrayOf("Powershell", "Stop-Computer")
                        os.contains("linux") || os.contains("mac") -> arrayOf("sudo shutdown -h now")
                        else -> throw UnsupportedOperationException("Unsupported operating system")
                    }
                    Runtime.getRuntime().exec(command)
                }
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to shutdown: ${e.message}",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }

        override fun configure(): Boolean = true
    }

    class RestartAction : TimeoutAction {
        override val name: String get() = "Restart Computer"

        override fun execute() {
            val result = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to restart the computer?",
                "Restart Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            )

            if (result == JOptionPane.YES_OPTION) {
                try {
                    val os = System.getProperty("os.name").lowercase()
                    val command = when {
                        os.contains("win") -> arrayOf("Powershell", "Restart-Computer")
                        os.contains("linux") || os.contains("mac") -> arrayOf("sudo reboot")
                        else -> throw UnsupportedOperationException("Unsupported operating system")
                    }
                    Runtime.getRuntime().exec(command)
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Failed to restart: ${e.message}",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }

        override fun configure(): Boolean = true
    }

    class NotificationAction : TimeoutAction {
        override val name: String get() = "Show Notification"

        override fun execute() {
            SystemTrayManager.showTimeUpNotification()
        }

        override fun configure(): Boolean = true
    }
}