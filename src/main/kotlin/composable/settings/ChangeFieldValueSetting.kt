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
fun changeFieldValueSettingItem(onSaveClick: (AppActions.SettingsScreen.SaveSettingsClick) -> Unit) {
    var changedFieldName by remember { mutableStateOf<String?>(null) }
    var changedFieldValue by remember { mutableStateOf<String>("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Замена значения поля")

        Spacer(modifier = Modifier.height(4.dp))

        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = changedFieldName ?: "",
                onValueChange = {
                    changedFieldName = it
                },
                label = { Text("название поля") },
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = changedFieldValue,
                onValueChange = {
                    changedFieldValue = it
                },
                label = { Text("новое значение поля") },
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(onClick = {
            val nnChangedFieldName = changedFieldName

            if (nnChangedFieldName.isNullOrEmpty()) {
                return@Button
            }

            onSaveClick(
                AppActions.SettingsScreen.SaveSettingsClick(
                    settingForChanges = IProxySetting.ChangeFieldValue(
                        changedFieldName = nnChangedFieldName,
                        changedFieldValue = changedFieldValue
                    )
                )
            )
        }) {
            Text("Сохранить это дерьмо")
        }

    }
}