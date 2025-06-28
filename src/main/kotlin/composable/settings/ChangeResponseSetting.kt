package composable.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import server.IProxySetting
import ui.action.AppActions


@Composable
fun changeResponseSettingItem(onSaveClick: (AppActions.SettingsScreen.SaveSettingsClick) -> Unit) {
    var url by remember { mutableStateOf<String?>(null) }
    var response by remember { mutableStateOf<String>("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Замена ответа")

        Spacer(modifier = Modifier.height(4.dp))

        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = url ?: "",
                onValueChange = {
                    url = it
                },
                label = { Text("url") },
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = response,
                onValueChange = {
                    response = it
                },
                label = { Text("измененный ответ") },
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(onClick = {
            val nnUrl = url

            if (nnUrl.isNullOrEmpty()) {
                return@Button
            }

            onSaveClick(
                AppActions.SettingsScreen.SaveSettingsClick(
                    settingForChanges = IProxySetting.ChangeResponse(
                        url = nnUrl,
                        response = response
                    )
                )
            )
        }) {
            Text("Сохранить это дерьмо")
        }

    }
}