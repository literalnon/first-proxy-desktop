package composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import composable.settings.changeFieldValueSettingItem
import composable.settings.changeResponseSettingItem
import composable.settings.changeTextSettingItem
import server.IProxySetting
import ui.ActiveScreenState
import ui.action.AppActions

@Composable
fun enableSettingsScreen(
    state: ActiveScreenState.LoadSettingsScreen,
    onSettingsClick: (AppActions.EnableSettingsScreen.SettingsClick) -> Unit,
    onLoadSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val allSettings = state.allSettingsFlow.collectAsState()
    val enableSettings = state.enableSettingsFlow.collectAsState()

    Row {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            item {
                Button(onClick = {
                    onBackClick()
                }) {
                    Text("Выйти")
                }
            }

            item {
                Text("Список добавленных настроек")
            }

            fillSettings(
                allSettings.value,
                onSettingsClick = onSettingsClick,
                enableSettings.value,
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Text("Список включенных настроек")
            }

            fillSettings(
                allSettings.value.filter { enableSettings.value.contains(it.id) },
                onSettingsClick = onSettingsClick,
                enableSettings.value,
            )
        }
    }

}

fun androidx.compose.foundation.lazy.LazyListScope.fillSettings(
    itemsValue: List<IProxySetting>,
    onSettingsClick: (AppActions.EnableSettingsScreen.SettingsClick) -> Unit,
    enabledSettingIds: List<Long>,
) {
    return items(itemsValue) { setting ->
        when (setting) {
            is IProxySetting.ChangeFieldValue -> {
                Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    Text("ChangeFieldValue. From ${setting.changedFieldName} to ${setting.changedFieldValue}")

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(onClick = {

                    }) {
                        Text("Удалить")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(onClick = {
                        onSettingsClick(AppActions.EnableSettingsScreen.SettingsClick(setting))
                    }) {
                        Text(
                            if (enabledSettingIds.contains(setting.id)) {
                                "Выключить"
                            } else {
                                "Включить"
                            }
                        )
                    }

                }
            }

            is IProxySetting.ChangeResponse -> {
                Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    Text("ChangeResponse. From ${setting.url} to ${setting.response}")

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(onClick = {

                    }) {
                        Text("Удалить")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(onClick = {
                        onSettingsClick(AppActions.EnableSettingsScreen.SettingsClick(setting))
                    }) {
                        Text(
                            if (enabledSettingIds.contains(setting.id)) {
                                "Выключить"
                            } else {
                                "Включить"
                            }
                        )
                    }
                }
            }

            is IProxySetting.ChangeText -> {
                Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    Text("ChangeText. From ${setting.beforeChangedString} to ${setting.afterChangedString}")

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(onClick = {

                    }) {
                        Text("Удалить")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(onClick = {
                        onSettingsClick(AppActions.EnableSettingsScreen.SettingsClick(setting))
                    }) {
                        Text(
                            if (enabledSettingIds.contains(setting.id)) {
                                "Выключить"
                            } else {
                                "Включить"
                            }
                        )
                    }
                }
            }

            is IProxySetting.ChangeDomain -> {
                Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    Text(
                        "ChangeDomain.\n" +
                                "domainNew ${setting.domainNew}\n" +
                                "domainOld ${setting.domainOld}"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(onClick = {

                    }) {
                        Text("Удалить")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(onClick = {
                        onSettingsClick(AppActions.EnableSettingsScreen.SettingsClick(setting))
                    }) {
                        Text(
                            if (enabledSettingIds.contains(setting.id)) {
                                "Выключить"
                            } else {
                                "Включить"
                            }
                        )
                    }
                }
            }
        }
    }
}