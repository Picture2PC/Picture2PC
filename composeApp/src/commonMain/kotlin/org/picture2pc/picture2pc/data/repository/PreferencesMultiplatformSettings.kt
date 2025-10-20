package org.picture2pc.picture2pc.data.repository

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanStateFlow
import com.russhwolf.settings.coroutines.getStringStateFlow
import kotlinx.coroutines.CoroutineScope
import org.picture2pc.picture2pc.domain.repository.PreferencesRepository

enum class SettingKeys {
    Name,
    Connectable
}

@OptIn(ExperimentalSettingsApi::class)
class PreferencesMultiplatformSettings(
    private val observableSettings: ObservableSettings,
    defaultCoroutineScope: CoroutineScope
) : PreferencesRepository {

    override var name =
        observableSettings.getStringStateFlow(defaultCoroutineScope, SettingKeys.Name.name, "")
    override var connectable = observableSettings.getBooleanStateFlow(
        defaultCoroutineScope,
        SettingKeys.Connectable.name, false
    )


    override suspend fun setName(name: String) {
        observableSettings.putString(SettingKeys.Name.name, name)
    }

    override suspend fun setConnectable(connectable: Boolean) {
        observableSettings.putBoolean(SettingKeys.Connectable.name, connectable)
    }
}