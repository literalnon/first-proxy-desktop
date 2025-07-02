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
fun changeDomainSettingItem(onSaveClick: (AppActions.SettingsScreen.SaveSettingsClick) -> Unit) {
    var domainOldString by remember { mutableStateOf<String?>(null) }
    var domainNewString by remember { mutableStateOf<String>("") }
    var groupName by remember { mutableStateOf<String>("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Замена domain")

        Spacer(modifier = Modifier.height(4.dp))

        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = domainOldString ?: "",
                onValueChange = {
                    domainOldString = it
                },
                label = { Text("изначальный domain") },
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = domainNewString,
                onValueChange = {
                    domainNewString = it
                },
                label = { Text("измененный domain") },
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = groupName,
                onValueChange = {
                    groupName = it
                },
                label = { Text("группа") },
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(onClick = {
            val nnDomainOld = domainOldString
            val nnDomainNew = domainNewString

            if (nnDomainOld.isNullOrEmpty() || nnDomainNew.isNullOrEmpty()) {
                return@Button
            }

            onSaveClick(
                AppActions.SettingsScreen.SaveSettingsClick(
                    settingForChanges = IProxySetting.ChangeDomain(
                        domainOld = nnDomainOld,
                        domainNew = nnDomainNew,
                        groupName = groupName
                    )
                )
            )
        }) {
            Text("Сохранить в список")
        }

    }
}