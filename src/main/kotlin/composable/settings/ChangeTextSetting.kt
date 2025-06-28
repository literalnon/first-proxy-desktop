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
fun changeTextSettingItem(onSaveClick: (AppActions.SettingsScreen.SaveSettingsClick) -> Unit) {
    var beforeChangedString by remember { mutableStateOf<String?>(null) }
    var afterChangedString by remember { mutableStateOf<String>("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Замена текста")

        Spacer(modifier = Modifier.height(4.dp))

        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = beforeChangedString ?: "",
                onValueChange = {
                    beforeChangedString = it
                },
                label = { Text("изначальный текст") },
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = afterChangedString,
                onValueChange = {
                    afterChangedString = it
                },
                label = { Text("измененный текст") },
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(onClick = {
            val nnBeforeChangedString = beforeChangedString

            if (nnBeforeChangedString.isNullOrEmpty()) {
                return@Button
            }

            onSaveClick(
                AppActions.SettingsScreen.SaveSettingsClick(
                    settingForChanges = IProxySetting.ChangeText(
                        beforeChangedString = nnBeforeChangedString,
                        afterChangedString = afterChangedString
                    )
                )
            )
        }) {
            Text("Сохранить это дерьмо")
        }

    }
}