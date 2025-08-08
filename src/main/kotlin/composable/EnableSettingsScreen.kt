package composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import server.IProxySetting
import store.ActiveScreenState
import store.action.AppActions

@OptIn(ExperimentalFoundationApi::class)
@Composable
@ExperimentalMaterialApi
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

@OptIn(ExperimentalFoundationApi::class)
fun androidx.compose.foundation.lazy.LazyListScope.fillSettings(
    itemsValue: List<IProxySetting>,
    onSettingsClick: (IProxySetting) -> Unit,
    enabledSettingIds: List<Long>,
    onRemoveSettingsClick: (IProxySetting) -> Unit,
) {
    val settingsMap = itemsValue.groupBy { it.groupName }

    settingsMap.forEach { groupName, settingsList ->
        stickyHeader {
            Text(
                groupName,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
        items(settingsList) { setting ->
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
                }
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

        Row {
            Button(
                modifier = Modifier.weight(1f)
                    .padding(end = 2.dp),
                onClick = {
                    onRemoveSettingsClick(setting)
                }) {
                Text("Удалить")
            }

            Button(
                modifier = Modifier.weight(1f)
                    .padding(start = 2.dp),
                onClick = {
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
}