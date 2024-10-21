package com.github.picture2pc.desktop.data.clientpreferences

import com.github.picture2pc.common.data.serverpreferences.PreferencesDataClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ClientPreferencesRepository(private val file: File) {
    private val _preferences = MutableStateFlow(PreferencesDataClass("", false))
    val preferences: StateFlow<PreferencesDataClass> = _preferences.asStateFlow()

    fun savePreferences() {
        file.writeText(Json.encodeToString(preferences.value))
    }

    fun loadPreferences() {
        _preferences.value = Json.decodeFromString(file.readText())
    }
}