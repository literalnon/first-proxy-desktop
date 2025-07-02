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
import com.fasterxml.jackson.core.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.*
import server.IProxySetting
import ui.ActiveScreenState
import ui.action.AppActions
import java.io.File
import java.nio.file.Files
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


@Composable
@ExperimentalMaterialApi
fun settingsScreen(
    state: ActiveScreenState.SettingsScreen,
    onSaveClick: (AppActions.SettingsScreen.SaveSettingsClick) -> Unit,
    onBackClick: () -> Unit,
    onLoadSettingsClick: (List<IProxySetting>) -> Unit,
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
                    onLoadSettingsClick(loadFile())
                }) {
                    Text("Загрузить настройки")
                }
            }

            item {
                Button(onClick = {
                    saveFileWithSwing(settings.value.map { encodeIProxySettingToJsonObject(it) }.toString())
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

fun loadFile(): List<IProxySetting> {

    // Создаем диалоговое окно выбора файла
    val fileChooser = JFileChooser()
    fileChooser.dialogTitle = "Выберите файл" // Заголовок окна
    fileChooser.fileFilter = FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt")

    // Показываем диалог (OPEN = выбор файла, SAVE = сохранение)
    val userSelection = fileChooser.showOpenDialog(null)


    // Если пользователь выбрал файл и нажал "Открыть"
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        val selectedFile = fileChooser.selectedFile
        println("JFileChooser Выбранный файл: " + selectedFile.absolutePath)

        // Далее можно прочитать файл, например:
        val fileContent = Files.readString(selectedFile.toPath())
        println("JFileChooser content: " + fileContent)
        try {
            val jsonObjects = Json.decodeFromString<JsonArray>(fileContent)
            return jsonObjects.map {
                decodeIProxySettingFromString(it.jsonObject)
            }
        } catch (e: Exception) {
            println("JFileChooser Ошибка парсинга: ${e.message}")
        }
    } else {
        println("JFileChooser Файл не выбран")
    }

    return listOf()
}

fun decodeIProxySettingFromString(jsonObject: JsonObject): IProxySetting {
    val name = jsonObject["settings_name"]?.jsonPrimitive?.contentOrNull

    return when (name) {
        IProxySetting.ChangeText::class.java.name -> {
            val groupName = jsonObject["group_name"]?.jsonPrimitive?.contentOrNull
            val beforeChangedString = jsonObject["before_changed_string"]?.jsonPrimitive?.contentOrNull
            val afterChangedString = jsonObject["after_changed_string"]?.jsonPrimitive?.contentOrNull

            if (groupName == null || beforeChangedString == null || afterChangedString == null) {
                throw RuntimeException(
                    "groupName == null ${groupName == null} " +
                            "beforeChangedString == null ${beforeChangedString == null} " +
                            "afterChangedString ${afterChangedString == null}"
                )
            }

            IProxySetting.ChangeText(
                groupName = groupName,
                beforeChangedString = beforeChangedString,
                afterChangedString = afterChangedString,
            )
        }

        IProxySetting.ChangeResponse::class.java.name -> {
            val groupName = jsonObject["group_name"]?.jsonPrimitive?.contentOrNull
            val url = jsonObject["url"]?.jsonPrimitive?.contentOrNull
            val response = jsonObject["response"]?.jsonPrimitive?.contentOrNull

            if (groupName == null || url == null || response == null) {
                throw RuntimeException(
                    "groupName == null ${groupName == null} " +
                            "url == null ${url == null} " +
                            "response ${response == null}"
                )
            }

            IProxySetting.ChangeResponse(
                groupName = groupName,
                url = url,
                response = response,
            )
        }

        IProxySetting.ChangeFieldValue::class.java.name -> {
            val groupName = jsonObject["group_name"]?.jsonPrimitive?.contentOrNull
            val changedFieldName = jsonObject["changed_field_name"]?.jsonPrimitive?.contentOrNull
            val changedFieldValue = jsonObject["changed_field_value"]?.jsonPrimitive?.contentOrNull

            if (groupName == null || changedFieldName == null || changedFieldValue == null) {
                throw RuntimeException(
                    "groupName == null ${groupName == null} " +
                            "changedFieldName == null ${changedFieldName == null} " +
                            "changedFieldValue == null ${changedFieldValue == null}"
                )
            }

            IProxySetting.ChangeFieldValue(
                groupName = groupName,
                changedFieldName = changedFieldName,
                changedFieldValue = changedFieldValue,
            )
        }

        else -> throw RuntimeException("settings_name is not IProxySetting")
    }
}

fun encodeIProxySettingToJsonObject(settings: IProxySetting): JsonObject = when (settings) {
    is IProxySetting.ChangeText -> {
        val content = HashMap<String, JsonElement>()

        content["settings_name"] = JsonPrimitive(IProxySetting.ChangeText::class.java.name)
        content["group_name"] = JsonPrimitive(settings.groupName)
        content["before_changed_string"] = JsonPrimitive(settings.beforeChangedString)
        content["after_changed_string"] = JsonPrimitive(settings.afterChangedString)

        JsonObject(content)
    }

    is IProxySetting.ChangeResponse -> {
        val content = HashMap<String, JsonElement>()

        content["settings_name"] = JsonPrimitive(IProxySetting.ChangeResponse::class.java.name)
        content["group_name"] = JsonPrimitive(settings.groupName)
        content["url"] = JsonPrimitive(settings.url)
        content["response"] = JsonPrimitive(settings.response)

        JsonObject(content)
    }

    is IProxySetting.ChangeFieldValue -> {
        val content = HashMap<String, JsonElement>()

        content["settings_name"] = JsonPrimitive(IProxySetting.ChangeText::class.java.name)
        content["group_name"] = JsonPrimitive(settings.groupName)
        content["changed_field_name"] = JsonPrimitive(settings.changedFieldName)
        content["changed_field_value"] = JsonPrimitive(settings.changedFieldValue)

        JsonObject(content)
    }
}

fun saveFileWithSwing(textForSave: String) {
    val fileChooser = JFileChooser().apply {
        dialogTitle = "Сохранить файл"
        // Устанавливаем начальную директорию
        currentDirectory = File(System.getProperty("user.home"))
        // Фильтр расширений (например, только .txt)
        fileFilter = FileNameExtensionFilter("Текстовые файлы", "txt")
    }

    val userSelection = fileChooser.showSaveDialog(null)

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        val fileToSave = fileChooser.selectedFile
        // Добавляем расширение, если его нет
        val finalFile = if (!fileToSave.name.endsWith(".txt")) {
            File("${fileToSave.absolutePath}.txt")
        } else {
            fileToSave
        }
        // Записываем данные
        finalFile.writeText(textForSave)
        println("Файл сохранён: ${finalFile.absolutePath}")
    } else {
        println("Сохранение отменено.")
    }
}

