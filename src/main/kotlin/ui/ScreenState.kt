package ui

import kotlinx.coroutines.flow.StateFlow
import net.lightbody.bmp.core.har.HarEntry
import server.IProxySetting
import java.util.concurrent.Flow

sealed interface ActiveScreenState {
    data class NotStartedScreen(
        val port: Int = 8080,
    ) : ActiveScreenState

    data class RequestsListsScreen(
        val requests: List<HarEntry>
    ) : ActiveScreenState

    data class RequestInfoScreen(
        val request: HarEntry
    ) : ActiveScreenState

    data class SettingsScreen(
        val settingsFlow: StateFlow<List<IProxySetting>>
    ) : ActiveScreenState
}