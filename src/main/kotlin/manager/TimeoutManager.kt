package me.cdh.manager

import me.cdh.container.TimeoutAction
import javax.swing.ButtonGroup
import javax.swing.JMenu
import javax.swing.JRadioButtonMenuItem

object TimeoutManager {
    const val NOTIFICATION = "notification"
    const val OPEN_APPLICATION = "openApplication"
    const val OPEN_WEB_PAGE = "openWebPage"
    const val LOCK_SCREEN = "lockScreen"
    const val SHUTDOWN = "shutDown"
    const val RESTART = "restart"
    private val actions = mapOf(
        NOTIFICATION to TimeoutAction.NotificationAction,
        OPEN_APPLICATION to TimeoutAction.OpenApplicationAction,
        OPEN_WEB_PAGE to TimeoutAction.OpenWebPageAction,
        LOCK_SCREEN to TimeoutAction.LockScreenAction,
        SHUTDOWN to TimeoutAction.ShutdownAction,
        RESTART to TimeoutAction.RestartAction
    )

    private val menuItems = mutableMapOf<String, JRadioButtonMenuItem>()
    private lateinit var bGroup: ButtonGroup
    private var isInitialized = false

    private var selectedAction: TimeoutAction = actions[NOTIFICATION]!!

    fun executeSelectedAction() {
        selectedAction.execute()
    }

    fun initialTimeoutMenu(menu: JMenu) {
        if (isInitialized) return
        bGroup = ButtonGroup()
        // create menu item
        createMenuItem(NOTIFICATION, "Show Notification", menu, true)
        createMenuItem(OPEN_APPLICATION, "Open Application", menu)
        createMenuItem(OPEN_WEB_PAGE, "Open Web Page", menu)
        createMenuItem(LOCK_SCREEN, "Lock Screen", menu)
        menu.addSeparator()
        createMenuItem(SHUTDOWN, "Shutdown Computer", menu)
        createMenuItem(RESTART, "Restart Computer", menu)

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