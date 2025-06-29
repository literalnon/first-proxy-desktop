package ui.action

import net.lightbody.bmp.core.har.HarEntry
import server.IProxySetting

sealed interface AppActions {
    sealed interface NotStartedScreen : AppActions {
        data class StartProxyClick(
            val port: Int,
        ) : NotStartedScreen
    }

    sealed interface RequestsListsScreen : AppActions {
        data class RequestInfoClick(
            val request: HarEntry
        ) : NotStartedScreen
    }

    sealed interface RequestInfoScreen : AppActions {
        data object BackNavigationClick : NotStartedScreen
    }

    sealed interface SideMenuScreen : AppActions {
        data object SettingsClick : NotStartedScreen
        data object EnableSettingsClick : NotStartedScreen
    }

    sealed interface SettingsScreen : AppActions {
        data class SaveSettingsClick(
            val settingForChanges: IProxySetting
        ) : SettingsScreen

        data object OnLoadSettingsClick : SettingsScreen

        data object BackNavigationClick : SettingsScreen
    }

    sealed interface EnableSettingsScreen : AppActions {
        data class SettingsClick(
            val settingForChanges: IProxySetting
        ) : SettingsScreen

        data object BackNavigationClick : SettingsScreen
    }
}