package me.cdh


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.cdh.palette.RainbowColorPicker
import java.awt.Color
import java.awt.Dimension
import java.awt.Image
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.event.ItemEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import javax.imageio.ImageIO
import javax.swing.*
import kotlin.system.exitProcess

object FlowContainer : JWindow() {
    val exit: JMenuItem by lazy(LazyThreadSafetyMode.NONE) {
        JMenuItem("Exit").apply { addActionListener { exitProcess(0) } }
    }
    val changeColor: JMenuItem by lazy(LazyThreadSafetyMode.NONE) {
        JMenuItem("Change Color").apply {
            addActionListener {
                RainbowColorPicker.showDialog(null, "Pick Color", Color.WHITE)?.let {
                    FlowLabel.foreground = it
                }
            }
        }
    }
    val countDown: JMenuItem by lazy(LazyThreadSafetyMode.NONE) {
        JMenuItem("Count Down").apply {
            addActionListener { FlowDialog.dialog.isVisible = true }
        }
    }
    val selectFont: JMenu by lazy(LazyThreadSafetyMode.NONE) {
        JMenu("Font Family").apply {
            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    FontMenuManager.initializeFontMenu(this@apply)
                }
            })
        }
    }
    val pin: JCheckBoxMenuItem by lazy(LazyThreadSafetyMode.NONE) {
        JCheckBoxMenuItem("Pin")
    }
    val menu: JPopupMenu by lazy(LazyThreadSafetyMode.NONE) {
        JPopupMenu().apply {
            add(countDown)
            add(changeColor)
            add(selectFont)
            add(pin)
            addSeparator()
            add(exit)
        }
    }
    var remainingTime = 0
    private lateinit var trayIcon: TrayIcon
    private lateinit var systemTray: SystemTray

    init {
        type = Type.UTILITY
        setSize(250, 80)
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        setLocation(screenSize.width - size.width - 100, screenSize.height - size.height - 160)
        background = Color(0.0f, 0.0f, 0.0f, 0.0f)
        isAlwaysOnTop = true
        isVisible = true
    }

    init {
        val mouseMotionAdapter = object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent?) {
                e ?: return
                setLocation(e.locationOnScreen.x - width / 2, e.locationOnScreen.y - height / 2)
            }
        }
        val mouseWheelAdapter = object : MouseAdapter() {
            override fun mouseWheelMoved(e: MouseWheelEvent?) {
                e ?: return
                val rotation = e.wheelRotation
                val currentFontSize = FlowLabel.font.size
                val newFontSize = currentFontSize + rotation

                // 设置字体大小的最小和最大限制
                val minFontSize = 8
                val maxFontSize = 72
                val clampedFontSize = newFontSize.coerceIn(minFontSize, maxFontSize)
                // 只有当字体大小在合理范围内时才更新
                if (clampedFontSize != currentFontSize) {
                    FlowLabel.font = FlowLabel.font.deriveFont(clampedFontSize.toFloat())

                    // 可选：根据字体大小调整窗口大小
                    val labelPreferredSize = FlowLabel.preferredSize
                    val newWidth = (labelPreferredSize.width * 1.2).toInt() // 留一些边距
                    val newHeight = (labelPreferredSize.height * 1.2).toInt()
                    size = Dimension(newWidth, newHeight)
                    // 重新验证和重绘组件
                    revalidate()
                    repaint()
                }
            }
        }
        addMouseMotionListener(mouseMotionAdapter)
        addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent?) {
                e ?: return
                if (e.button == MouseEvent.BUTTON3 || e.isPopupTrigger) {
                    menu.show(e.component, e.x, e.y)
                }
            }
        })
        addMouseWheelListener(mouseWheelAdapter)
        pin.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                removeMouseMotionListener(mouseMotionAdapter)
                removeMouseWheelListener(mouseWheelAdapter)
            } else if (e.stateChange == ItemEvent.DESELECTED) {
                addMouseMotionListener(mouseMotionAdapter)
                addMouseWheelListener(mouseWheelAdapter)
            }
        }
    }

    fun initSystemTray() {
        if (!SystemTray.isSupported()) return
        CoroutineScope(Dispatchers.IO).launch {
            systemTray = SystemTray.getSystemTray()
            trayIcon = TrayIcon(
                ImageIO.read(javaClass.classLoader.getResourceAsStream("kotlin.png")).getScaledInstance(
                    systemTray.trayIconSize.width, systemTray.trayIconSize.height, Image.SCALE_SMOOTH
                ), "Flow-White"
            )
            withContext(Dispatchers.Main) {
                systemTray.add(trayIcon)
            }
        }
    }

    fun showTimeUpNotification() {
        if (SystemTray.isSupported()) {
            trayIcon.displayMessage(
                "Time's up!", "Countdown ended.", TrayIcon.MessageType.INFO
            )
        }
    }


    @Suppress
    private fun readResolve(): Any = FlowContainer
}