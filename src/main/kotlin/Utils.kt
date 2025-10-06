package me.cdh

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.swing.JMenuItem
import javax.swing.JOptionPane

fun JMenuItem.onClickAsync(block: suspend CoroutineScope.() -> Unit): JMenuItem {
    addActionListener {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                block()
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    null,
                    "Operation failed: ${e.message}",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }
    return this
}