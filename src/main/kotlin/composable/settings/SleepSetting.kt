package composable.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import server.IProxySetting
import store.action.AppActions


@Composable
fun sleepSettingItem(onSaveClick: (AppActions.SettingsScreen.SaveSettingsClick) -> Unit) {
    var urlValue by remember { mutableStateOf<String?>(null) }
    var sleepValue by remember { mutableStateOf<String?>(null) }
    var groupName by remember { mutableStateOf<String>("") }
    var settingName by remember { mutableStateOf<String>("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Замедление ответа")

        Spacer(modifier = Modifier.height(4.dp))

        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = urlValue ?: "",
                onValueChange = {
                    urlValue = it
                },
                label = { Text("url") },
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = sleepValue ?: "",
                onValueChange = {
                    sleepValue = it
                },
                label = { Text("на сколько замедлить, мс") },
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = groupName,
                onValueChange = {
                    groupName = it
                },
                label = { Text("группа") },
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = settingName,
                onValueChange = {
                    settingName = it
                },
                label = { Text("название настройки") },
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val nnSleepValue = sleepValue?.toLongOrNull()
                val nnUrlValue = urlValue

                if (nnUrlValue == null || nnSleepValue == null || nnSleepValue <= 0) {
                    return@Button
                }

                onSaveClick(
                    AppActions.SettingsScreen.SaveSettingsClick(
                        settingForChanges = IProxySetting.Sleep(
                            time = nnSleepValue,
                            groupName = groupName,
                            settingName = settingName,
                            url = nnUrlValue,
                        )
                    )
                )
            }) {
            Text("Сохранить в список")
        }

    }
}