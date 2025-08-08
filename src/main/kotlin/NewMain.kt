import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import composable.enableSettingsScreen
import composable.notStartedScreen
import composable.requestInfoScreen
import composable.requestsListsScreen
import composable.settings.settingsScreen
import kotlinx.coroutines.*
import server.FullFeaturedProxy
import server.settings.SettingsDataStore
import store.ActiveScreenState
import store.action.AppActions
import store.store.ScreenStore

@ExperimentalMaterialApi
fun main() = application {
    val storeCoroutineScope = CoroutineScope(Dispatchers.Default)
    val settingsDataStore = SettingsDataStore(storeCoroutineScope)
    val proxyServer = FullFeaturedProxy(settingsDataStore = settingsDataStore)

    val screenStore = ScreenStore(
        storeCoroutineScope = storeCoroutineScope,
        settingsDataStore = settingsDataStore,
        proxyServer = proxyServer
    )

    val screenState = screenStore.screenStateFlow.collectAsState()

    Window(onCloseRequest = ::exitApplication) {
        AppTheme {
            Row {
                sideMenu(screenStore)

                Box(
                    modifier = Modifier.padding(16.dp),
                ) {
                    DrawScreen(
                        screenState = screenState.value,
                        screenStore = screenStore,
                        coroutineScope = storeCoroutineScope,
                    )
                }
            }
        }
    }
}

@Composable
fun sideMenu(screenStore: ScreenStore) {
    Column(
        modifier = Modifier.fillMaxWidth(0.25f)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                screenStore.setAction(AppActions.SideMenuScreen.MainScreenClick)
            }) {
            Text("Главная")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                screenStore.setAction(AppActions.SideMenuScreen.SettingsClick)
            }) {
            Text("Добавить настройку")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                screenStore.setAction(AppActions.SideMenuScreen.EnableSettingsClick)
            }) {
            Text("Активировать настройки")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                screenStore.setAction(AppActions.SideMenuScreen.CertScreenClick)
            }) {
            Text("Сертификат")
        }
    }
}

@Composable
@ExperimentalMaterialApi
fun DrawScreen(
    screenState: ActiveScreenState,
    screenStore: ScreenStore,
    coroutineScope: CoroutineScope
) {
    when (screenState) {
        is ActiveScreenState.NotStartedScreen -> {
            notStartedScreen(
                state = screenState,
                startButtonClick = { startProxyClick: AppActions.NotStartedScreen.StartProxyClick ->
                    screenStore.setAction(startProxyClick)
                })
        }

        is ActiveScreenState.RequestInfoScreen -> {
            requestInfoScreen(
                state = screenState,
                onBackClick = {
                    screenStore.setAction(AppActions.RequestInfoScreen.BackNavigationClick)
                }
            )
        }

        is ActiveScreenState.RequestsListsScreen -> {
            requestsListsScreen(
                state = screenState,
                requestInfoClick = {
                    screenStore.setAction(AppActions.RequestsListsScreen.RequestInfoClick(it))
                }
            )
        }

        is ActiveScreenState.SettingsScreen -> {
            settingsScreen(
                state = screenState,
                onSaveClick = { saveSettingsClick ->
                    screenStore.setAction(saveSettingsClick)
                },
                onBackClick = {
                    screenStore.setAction(AppActions.SettingsScreen.BackNavigationClick)
                },
                onLoadSettingsClick = {
                    screenStore.setAction(AppActions.SettingsScreen.OnLoadSettingsClick(it))
                },
                onSaveSettingsClick = {
                    TODO()
                },
                coroutineScope = coroutineScope,
                onRemoveSettingsClick = {
                    screenStore.setAction(AppActions.SettingsScreen.OnRemoveSettingsClick(it))
                },
                onEnableSettingsClick = { onSettingsClick ->
                    screenStore.setAction(AppActions.EnableSettingsScreen.SettingsClick(onSettingsClick))
                }
            )
        }

        is ActiveScreenState.LoadSettingsScreen -> {
            enableSettingsScreen(
                state = screenState,
                onSettingsClick = { onSettingsClick ->
                    screenStore.setAction(AppActions.EnableSettingsScreen.SettingsClick(onSettingsClick))
                },
                onBackClick = {
                    screenStore.setAction(AppActions.EnableSettingsScreen.BackNavigationClick)
                },
                onLoadSettingsClick = {
                    screenStore.setAction(AppActions.SettingsScreen.OnLoadSettingsClick(it))
                },
                onRemoveSettingsClick = {
                    screenStore.setAction(AppActions.SettingsScreen.OnRemoveSettingsClick(it))
                }
            )
        }
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}

