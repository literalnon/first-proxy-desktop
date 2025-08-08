package composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import server.IProxySetting
import store.ActiveScreenState
import store.action.AppActions

@Composable
fun enableSettingsScreen(
    state: ActiveScreenState.LoadSettingsScreen,
    onSettingsClick: (IProxySetting) -> Unit,
    onLoadSettingsClick: (List<IProxySetting>) -> Unit,
    onRemoveSettingsClick: (IProxySetting) -> Unit,
    onBackClick: () -> Unit,
) {
    val allSettings = state.allSettingsFlow.collectAsState()
    val enableSettings = state.enableSettingsFlow.collectAsState()

    Row {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            item {
                Text("Список добавленных настроек")
            }

            fillSettings(
                allSettings.value,
                onSettingsClick = onSettingsClick,
                enableSettings.value,
                onRemoveSettingsClick = onRemoveSettingsClick,
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
                onRemoveSettingsClick = onRemoveSettingsClick,
            )
        }
    }

}

fun androidx.compose.foundation.lazy.LazyListScope.fillSettings(
    itemsValue: List<IProxySetting>,
    onSettingsClick: (IProxySetting) -> Unit,
    enabledSettingIds: List<Long>,
    onRemoveSettingsClick: (IProxySetting) -> Unit,
) {
    return items(itemsValue) { setting ->
        when (setting) {
            is IProxySetting.ChangeFieldValue -> {
                fillOneSetting(
                    setting = setting,
                    settingName = setting.settingName,
                    settingFeature = "ChangeFieldValue. From ${setting.changedFieldName} to ${setting.changedFieldValue}",
                    onSettingsClick = onSettingsClick,
                    enabledSettingIds = enabledSettingIds,
                    onRemoveSettingsClick = onRemoveSettingsClick,
                )
//                Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
//                    Text(
//                        setting.settingName,
//                        fontSize = 14.sp,
//                    )
//
//                    Text(
//                        "ChangeFieldValue. From ${setting.changedFieldName} to ${setting.changedFieldValue}",
//                        fontSize = 12.sp,
//                    )
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Button(onClick = {
//                        println("onRemoveSettingsClick fillSettings")
//
//                        onRemoveSettingsClick(setting)
//                    }) {
//                        Text("Удалить")
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Button(onClick = {
//                        onSettingsClick(setting)
//                    }) {
//                        Text(
//                            if (enabledSettingIds.contains(setting.id)) {
//                                "Выключить"
//                            } else {
//                                "Включить"
//                            }
//                        )
//                    }
//
//                }
            }

            is IProxySetting.ChangeResponse -> {
                fillOneSetting(
                    setting = setting,
                    settingName = setting.settingName,
                    settingFeature = "ChangeResponse. For ${setting.url}",
                    onSettingsClick = onSettingsClick,
                    enabledSettingIds = enabledSettingIds,
                    onRemoveSettingsClick = onRemoveSettingsClick,
                )

//                Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
//                    Text(
//                        "ChangeResponse. For ${setting.url}",
//                        fontSize = 14.sp,
//                    )
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Button(onClick = {
//                        println("onRemoveSettingsClick fillSettings")
//
//                        onRemoveSettingsClick(setting)
//                    }) {
//                        Text("Удалить")
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Button(onClick = {
//                        onSettingsClick(setting)
//                    }) {
//                        Text(
//                            if (enabledSettingIds.contains(setting.id)) {
//                                "Выключить"
//                            } else {
//                                "Включить"
//                            }
//                        )
//                    }
//                }
            }

            is IProxySetting.ChangeText -> {
                fillOneSetting(
                    setting = setting,
                    settingName = setting.settingName,
                    settingFeature = "ChangeText. From ${setting.beforeChangedString} to ${setting.afterChangedString}",
                    onSettingsClick = onSettingsClick,
                    enabledSettingIds = enabledSettingIds,
                    onRemoveSettingsClick = onRemoveSettingsClick,
                )
//                Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
//                    Text(
//                        "ChangeText. From ${setting.beforeChangedString} to ${setting.afterChangedString}",
//                        fontSize = 14.sp,
//                    )
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Button(onClick = {
//                        println("onRemoveSettingsClick fillSettings")
//
//                        onRemoveSettingsClick(setting)
//                    }) {
//                        Text("Удалить")
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Button(onClick = {
//                        onSettingsClick(setting)
//                    }) {
//                        Text(
//                            if (enabledSettingIds.contains(setting.id)) {
//                                "Выключить"
//                            } else {
//                                "Включить"
//                            }
//                        )
//                    }
//                }
            }

            is IProxySetting.ChangeDomain -> {
                fillOneSetting(
                    setting = setting,
                    settingName = setting.settingName,
                    settingFeature = "ChangeDomain.\n" +
                            "domainNew ${setting.domainNew}\n" +
                            "domainOld ${setting.domainOld}",
                    onSettingsClick = onSettingsClick,
                    enabledSettingIds = enabledSettingIds,
                    onRemoveSettingsClick = onRemoveSettingsClick,
                )
//                Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
//                    Text(
//                        "ChangeDomain.\n" +
//                                "domainNew ${setting.domainNew}\n" +
//                                "domainOld ${setting.domainOld}",
//                        fontSize = 14.sp,
//                    )
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Button(onClick = {
//                        println("onRemoveSettingsClick fillSettings")
//
//                        onRemoveSettingsClick(setting)
//                    }) {
//                        Text("Удалить")
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Button(onClick = {
//                        println("onSettingsClick fillSettings")
//                        onSettingsClick(setting)
//                    }) {
//                        Text(
//                            if (enabledSettingIds.contains(setting.id)) {
//                                "Выключить"
//                            } else {
//                                "Включить"
//                            }
//                        )
//                    }
//                }
            }
        }
    }
}


@Composable
fun fillOneSetting(
    setting: IProxySetting,
    settingName: String,
    settingFeature: String,
    onSettingsClick: (IProxySetting) -> Unit,
    enabledSettingIds: List<Long>,
    onRemoveSettingsClick: (IProxySetting) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        Text(
            settingName,
            fontSize = 14.sp,
        )

        Text(
            settingFeature,
            fontSize = 12.sp,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(onClick = {
            onRemoveSettingsClick(setting)
        }) {
            Text("Удалить")
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(onClick = {
            onSettingsClick(setting)
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