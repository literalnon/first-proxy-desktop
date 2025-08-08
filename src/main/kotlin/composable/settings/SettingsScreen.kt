package composable.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import composable.fillSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.*
import server.IProxySetting
import store.ActiveScreenState
import store.action.AppActions
import java.io.File
import java.nio.file.Files
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


@OptIn(ExperimentalFoundationApi::class)
@Composable
@ExperimentalMaterialApi
fun settingsScreen(
    state: ActiveScreenState.SettingsScreen,
    onSaveClick: (AppActions.SettingsScreen.SaveSettingsClick) -> Unit,
    onBackClick: () -> Unit,
    onLoadSettingsClick: (List<IProxySetting>) -> Unit,
    onSaveSettingsClick: () -> Unit,
    onRemoveSettingsClick: (IProxySetting) -> Unit,
    onEnableSettingsClick: (IProxySetting) -> Unit,
    coroutineScope: CoroutineScope
) {
    val settings = state.settingsFlow.collectAsState()
    val enableSettings = state.enableSettingsFlow.collectAsState()

    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onLoadSettingsClick(loadFile())
                    }) {
                    Text("Загрузить настройки из файла")
                }
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
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

            item {
                changeDomainSettingItem(onSaveClick)
            }
        }

        Spacer(Modifier.width(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Text(
                    "Список добавленных настроек",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            val settingsMap = settings.value.groupBy { it.groupName }

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

                fillSettings(
                    settingsList,
                    onSettingsClick = onEnableSettingsClick,
                    enableSettings.value,
                    onRemoveSettingsClick = onRemoveSettingsClick,
                )
//                items(settingsList) { setting ->
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp),
//                    ) {
//                        /
//                        when (setting) {
//                            is IProxySetting.ChangeFieldValue -> {
//
//                                Text(
//                                    "ChangeFieldValue\n" +
//                                            "From ${setting.changedFieldName}\n" +
//                                            "to ${setting.changedFieldValue}",
//                                    modifier = Modifier.fillMaxWidth(),
//                                    fontSize = 14.sp,
//                                )
//
//                                Spacer(modifier = Modifier.height(4.dp))
//
//                                Button(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    onClick = {
//                                        onRemoveSettingsClick(setting)
//                                    }
//                                ) {
//                                    Text("Удалить")
//                                }
//                            }
//
//                            is IProxySetting.ChangeResponse -> {
//
//                                Text(
//                                    "ChangeResponse\n" +
//                                            "From ${setting.url}\n" +
//                                            "to ${setting.response}",
//                                    modifier = Modifier.fillMaxWidth(),
//                                    fontSize = 14.sp,
//                                )
//
//                                Spacer(modifier = Modifier.height(4.dp))
//
//                                Button(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    onClick = {
//                                        onRemoveSettingsClick(setting)
//                                    }
//                                ) {
//                                    Text("Удалить")
//                                }
//                            }
//
//                            is IProxySetting.ChangeText -> {
//                                Text(
//                                    "ChangeText\n" +
//                                            "From ${setting.beforeChangedString}\n" +
//                                            "to ${setting.afterChangedString}",
//                                    modifier = Modifier.fillMaxWidth(),
//                                    fontSize = 14.sp,
//                                )
//
//                                Spacer(modifier = Modifier.height(4.dp))
//
//                                Button(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    onClick = {
//                                        onRemoveSettingsClick(setting)
//                                    }
//                                ) {
//                                    Text("Удалить")
//                                }
//                            }
//
//                            is IProxySetting.ChangeDomain -> {
//                                Text(
//                                    "ChangeDomain\n" +
//                                            "domainOld ${setting.domainOld}\n" +
//                                            "domainNew ${setting.domainNew}",
//                                    modifier = Modifier.fillMaxWidth(),
//                                    fontSize = 14.sp,
//                                )
//
//                                Spacer(modifier = Modifier.height(4.dp))
//
//                                Button(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    onClick = {
//                                        onRemoveSettingsClick(setting)
//                                    }
//                                ) {
//                                    Text("Удалить")
//                                }
//                            }
//                        }
//                    }
//                }
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
    val settingName = jsonObject["setting_name"]?.jsonPrimitive?.contentOrNull ?: "Без названия"
    val groupName = jsonObject["group_name"]?.jsonPrimitive?.contentOrNull

    return when (name) {
        IProxySetting.ChangeText::class.java.name -> {
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
                settingName = settingName,
            )
        }

        IProxySetting.ChangeResponse::class.java.name -> {
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
                settingName = settingName,
            )
        }

        IProxySetting.ChangeFieldValue::class.java.name -> {
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
                settingName = settingName,
            )
        }

        IProxySetting.ChangeDomain::class.java.name -> {
            val domainNew = jsonObject["domain_new"]?.jsonPrimitive?.contentOrNull
            val domainOld = jsonObject["domain_old"]?.jsonPrimitive?.contentOrNull

            if (groupName == null || domainNew == null || domainOld == null) {
                throw RuntimeException(
                    "groupName == null ${groupName == null} " +
                            "changedFieldName == null ${domainNew == null} " +
                            "changedFieldValue == null ${domainOld == null}"
                )
            }

            IProxySetting.ChangeDomain(
                groupName = groupName,
                domainNew = domainNew,
                domainOld = domainOld,
                settingName = settingName,
            )
        }

        else -> throw RuntimeException("settings_name is not IProxySetting")
    }
}

fun encodeIProxySettingToJsonObject(settings: IProxySetting): JsonObject {
    val content = HashMap<String, JsonElement>()
    content["setting_name"] = JsonPrimitive(settings.settingName)
    content["group_name"] = JsonPrimitive(settings.groupName)

    when (settings) {
        is IProxySetting.ChangeText -> {
            content["settings_name"] = JsonPrimitive(IProxySetting.ChangeText::class.java.name)
            content["before_changed_string"] = JsonPrimitive(settings.beforeChangedString)
            content["after_changed_string"] = JsonPrimitive(settings.afterChangedString)
        }

        is IProxySetting.ChangeResponse -> {
            content["settings_name"] = JsonPrimitive(IProxySetting.ChangeResponse::class.java.name)
            content["url"] = JsonPrimitive(settings.url)
            content["response"] = JsonPrimitive(settings.response)
        }

        is IProxySetting.ChangeFieldValue -> {
            content["settings_name"] = JsonPrimitive(IProxySetting.ChangeText::class.java.name)
            content["changed_field_name"] = JsonPrimitive(settings.changedFieldName)
            content["changed_field_value"] = JsonPrimitive(settings.changedFieldValue)
        }

        is IProxySetting.ChangeDomain -> {
            content["settings_name"] = JsonPrimitive(IProxySetting.ChangeDomain::class.java.name)
            content["domain_new"] = JsonPrimitive(settings.domainNew)
            content["domain_old"] = JsonPrimitive(settings.domainOld)
        }
    }

    return JsonObject(content)
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

