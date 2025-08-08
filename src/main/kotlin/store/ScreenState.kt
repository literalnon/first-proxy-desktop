package store

import kotlinx.coroutines.flow.StateFlow
import net.lightbody.bmp.core.har.HarEntry
import server.IProxySetting

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
        val settingsFlow: StateFlow<List<IProxySetting>>,
        val enableSettingsFlow: StateFlow<List<Long>>,
    ) : ActiveScreenState

    data class LoadSettingsScreen(
        val enableSettingsFlow: StateFlow<List<Long>>,
        val allSettingsFlow: StateFlow<List<IProxySetting>>,
    ) : ActiveScreenState
}