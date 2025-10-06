package me.cdh.manager

import me.cdh.container.TimeoutAction
import javax.swing.ButtonGroup
import javax.swing.JMenu
import javax.swing.JRadioButtonMenuItem

object TimeoutManager {
    object Type {
        const val NOTIFICATION = "notification"
        const val OPEN_APPLICATION = "openApplication"
        const val OPEN_WEB_PAGE = "openWebPage"
        const val SHUTDOWN = "shutDown"
        const val RESTART = "restart"
    }

    private val actions = mapOf(
        Type.NOTIFICATION to TimeoutAction.NotificationAction(),
        Type.OPEN_APPLICATION to TimeoutAction.OpenApplicationAction(),
        Type.OPEN_WEB_PAGE to TimeoutAction.OpenWebPageAction(),
        Type.SHUTDOWN to TimeoutAction.ShutdownAction(),
        Type.RESTART to TimeoutAction.RestartAction()
    )

    private val menuItems = mutableMapOf<String, JRadioButtonMenuItem>()
    private lateinit var bGroup: ButtonGroup
    private var isInitialized = false

    var selectedAction: TimeoutAction = actions[Type.NOTIFICATION]!!
        private set

    fun executeSelectedAction() {
        selectedAction.execute()
    }

    fun initialTimeoutMenu(menu: JMenu) {
        if (isInitialized) return
        bGroup = ButtonGroup()
        // create menu item
        createMenuItem(Type.NOTIFICATION, "Show Notification", menu, true)
        createMenuItem(Type.OPEN_APPLICATION, "Open Application", menu)
        createMenuItem(Type.OPEN_WEB_PAGE, "Open Web Page", menu)
        menu.addSeparator()
        createMenuItem(Type.SHUTDOWN, "Shutdown Computer", menu)
        createMenuItem(Type.RESTART, "Restart Computer", menu)

        isInitialized = true
    }

    private fun createMenuItem(actionKey: String, displayName: String, menu: JMenu, selected: Boolean = false) {
        val menuItem = JRadioButtonMenuItem(displayName)
        menuItem.isSelected = selected
        menuItem.addActionListener {
            selectedAction = actions[actionKey]!!
            updateMenuConfiguration(actionKey)
        }
        menuItems[actionKey] = menuItem
        bGroup.add(menuItem)
        menu.add(menuItem)
        if (selected) selectedAction = actions[actionKey]!!
    }

    private fun updateMenuConfiguration(actionsKey: String) {
        when (actionsKey) {
            "openApplication", "openWebPage" -> {
                selectedAction.configure()
            }
        }
    }
}