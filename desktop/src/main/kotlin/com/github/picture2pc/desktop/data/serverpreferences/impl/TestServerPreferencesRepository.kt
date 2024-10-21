package com.github.picture2pc.desktop.data.serverpreferences.impl

import com.github.picture2pc.common.data.serverpreferences.PreferencesDataClass
import com.github.picture2pc.common.data.serverpreferences.ServerPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class TestServerPreferencesRepository(initialName: String) : ServerPreferencesRepository() {
    private val _preferences =
        MutableStateFlow(
            PreferencesDataClass(initialName, false)
        )
    override val preferences: StateFlow<PreferencesDataClass> = _preferences.asStateFlow()

    private val preferenceFile = File("preferences.json")

    override fun savePreferences() {
        preferences.onEach {
            preferenceFile.writeText(Json.encodeToString(preferences))
        }
    }

    override fun loadPreferences() {
        _preferences.value = Json.decodeFromString(preferenceFile.readText())
    }
}