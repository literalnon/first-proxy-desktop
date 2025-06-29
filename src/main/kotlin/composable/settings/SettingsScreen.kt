package composable.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import server.IProxySetting
import ui.ActiveScreenState
import ui.action.AppActions
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


@Composable
@ExperimentalMaterialApi
fun settingsScreen(
    state: ActiveScreenState.SettingsScreen,
    onSaveClick: (AppActions.SettingsScreen.SaveSettingsClick) -> Unit,
    onBackClick: () -> Unit,
    onLoadSettingsClick: () -> Unit,
    onSaveSettingsClick: () -> Unit,
    coroutineScope: CoroutineScope
) {
    val settings = state.settingsFlow.collectAsState()

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
                Button(onClick = {
                    //onLoadSettingsClick()
                    loadFile()
                }) {
                    Text("Загрузить настройки")
                }
            }

            item {
                Button(onClick = {
                    onSaveSettingsClick()
                }) {
                    Text("Сохранить настройки в файл")
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
            item {
                Text("Список добавленных настроек")
            }

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

fun loadFile() {

    // Создаем диалоговое окно выбора файла
    val fileChooser = JFileChooser()
    fileChooser.dialogTitle = "Выберите файл" // Заголовок окна
    fileChooser.fileFilter = FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt")

    // Показываем диалог (OPEN = выбор файла, SAVE = сохранение)
    val userSelection = fileChooser.showOpenDialog(null)


    // Если пользователь выбрал файл и нажал "Открыть"
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        val selectedFile = fileChooser.selectedFile
        println("Выбранный файл: " + selectedFile.absolutePath)

        // Далее можно прочитать файл, например:
        // String content = Files.readString(selectedFile.toPath());
        // byte[] bytes = Files.readAllBytes(selectedFile.toPath());
    } else {
        println("Файл не выбран")
    }
}

