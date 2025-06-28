package ui.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import server.FullFeaturedProxy
import ui.ActiveScreenState
import ui.action.AppActions

class ScreenStore(
    private val storeCoroutineScope: CoroutineScope
) {
    private val proxyServer = FullFeaturedProxy()

    val screenStateFlow: MutableStateFlow<ActiveScreenState> = MutableStateFlow<ActiveScreenState>(
        ActiveScreenState.NotStartedScreen()
    )

    private val screenStateReplayCache = arrayListOf<ActiveScreenState>(ActiveScreenState.NotStartedScreen()).apply {
        storeCoroutineScope.launch {
            screenStateFlow.collect {
                this@apply.add(it)
            }
        }
    }

    fun setAction(action: AppActions) {
        println("ScreenStore setAction ${action}")
        println("ScreenStore screenStateFlow ${screenStateFlow.replayCache}")

        when (action) {
            is AppActions.NotStartedScreen.StartProxyClick -> {
                storeCoroutineScope.launch {
                    when (screenStateFlow.value) {
                        is ActiveScreenState.NotStartedScreen -> {
                            proxyServer.start(action.port)
                            screenStateFlow.emit(ActiveScreenState.RequestsListsScreen(proxyServer.requests.value))
                            proxyServer.requests.collect {
                                when (screenStateFlow.value) {
                                    is ActiveScreenState.NotStartedScreen,
                                    is ActiveScreenState.SettingsScreen,
                                    is ActiveScreenState.RequestInfoScreen -> {
                                    }

                                    is ActiveScreenState.RequestsListsScreen -> {
                                        screenStateFlow.emit(ActiveScreenState.RequestsListsScreen(it))
                                    }
                                }
                            }
                        }

                        is ActiveScreenState.RequestInfoScreen,
                        is ActiveScreenState.RequestsListsScreen -> TODO()

                        is ActiveScreenState.SettingsScreen -> TODO()
                    }
                }
            }

            AppActions.RequestInfoScreen.BackNavigationClick -> {
                storeCoroutineScope.launch {
                    screenStateFlow.emit(ActiveScreenState.RequestsListsScreen(proxyServer.requests.value))
                }
            }

            is AppActions.RequestsListsScreen.RequestInfoClick -> {
                storeCoroutineScope.launch {
                    screenStateFlow.emit(ActiveScreenState.RequestInfoScreen(action.request))
                }
            }

            AppActions.SideMenuScreen.SettingsClick -> {
                storeCoroutineScope.launch {
                    screenStateFlow.emit(ActiveScreenState.SettingsScreen(settingsFlow = proxyServer.settings))
                }
            }

            AppActions.SettingsScreen.BackNavigationClick -> {
                storeCoroutineScope.launch {
                    screenStateFlow.emit(
                        screenStateReplayCache[screenStateReplayCache.size - 2]
                    )
                }
            }

            is AppActions.SettingsScreen.SaveSettingsClick -> {
                proxyServer.addActiveSettings(action.settingForChanges)
            }
        }
    }
}