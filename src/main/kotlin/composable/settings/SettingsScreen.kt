package composable.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import server.IProxySetting
import ui.ActiveScreenState
import ui.action.AppActions

@Composable
@ExperimentalMaterialApi
fun settingsScreen(
    state: ActiveScreenState.SettingsScreen,
    onSaveClick: (AppActions.SettingsScreen.SaveSettingsClick) -> Unit,
    onBackClick: () -> Unit,
    coroutineScope: CoroutineScope
) {
    val settings = state.settingsFlow.collectAsState()

//    by remember { mutableStateOf(state.settingsFlow.value) }.apply {
//        coroutineScope.launch {
//            state.settingsFlow.collect {
//                this@apply = it
//            }
//        }
//    }



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
                changeFieldValueSettingItem(onSaveClick)
            }

            item {
                changeTextSettingItem(onSaveClick)
            }

            item {
                changeResponseSettingItem(onSaveClick)
            }

        }

        Spacer(Modifier.width(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            items(settings.value) { setting ->
                when (setting) {
                    is IProxySetting.ChangeFieldValue -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text("ChangeFieldValue. From ${setting.changedFieldName} to ${setting.changedFieldValue}")

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(onClick = {

                            }) {
                                Text("Удалить")
                            }
                        }
                    }

                    is IProxySetting.ChangeResponse -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text("ChangeResponse. From ${setting.url} to ${setting.response}")

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(onClick = {

                            }) {
                                Text("Удалить")
                            }
                        }
                    }

                    is IProxySetting.ChangeText -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text("ChangeText. From ${setting.beforeChangedString} to ${setting.afterChangedString}")

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(onClick = {

                            }) {
                                Text("Удалить")
                            }
                        }
                    }
                }
            }
        }
    }
}

