package server.settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import server.IProxySetting

class SettingsDataStore(
    private val storeCoroutineScope: CoroutineScope
) {
    val allSettings = MutableStateFlow(arrayListOf<IProxySetting>())
    val enabledSettingIds = MutableStateFlow(arrayListOf<Long>())

    fun addSetting(setting: IProxySetting) {
        storeCoroutineScope.launch {
            allSettings.emit(
                arrayListOf<IProxySetting>().apply {
                    addAll(allSettings.value)
                    add(setting)
                }

            )
        }
    }

    fun removeSetting(setting: IProxySetting) {
        storeCoroutineScope.launch {
            allSettings.emit(
                arrayListOf<IProxySetting>().apply {
                    addAll(allSettings.value.filter { it.id != setting.id })
                }
            )

            enabledSettingIds.emit(
                arrayListOf<Long>().apply {
                    addAll(enabledSettingIds.value.filter { it != setting.id })
                }
            )
        }
    }

    fun changeEnabledSetting(setting: IProxySetting) {
        storeCoroutineScope.launch {
            enabledSettingIds.emit(
                arrayListOf<Long>().apply {
                    addAll(enabledSettingIds.value)
                    if (enabledSettingIds.value.contains(setting.id)) {
                        remove(setting.id)
                    } else {
                        add(setting.id)
                    }
                }
            )
        }
    }

    fun loadedSettings(settings: List<IProxySetting>) {
        storeCoroutineScope.launch {
            allSettings.emit(
                arrayListOf<IProxySetting>().apply {
                    addAll(allSettings.value)
                    addAll(settings)
                }
            )
        }
    }
}