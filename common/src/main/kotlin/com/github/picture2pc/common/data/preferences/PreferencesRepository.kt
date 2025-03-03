package com.github.picture2pc.common.data.preferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
data class Preferences(var name: String, var connectable: Boolean)

abstract class PreferencesRepository {
    private val _preferences =
        MutableStateFlow(Preferences(PreferencesDefaults.NAME, PreferencesDefaults.CONNECTABLE))
    val preferences: StateFlow<Preferences> = _preferences

    fun setPreferences(name: String, connectable: Boolean) {
        _preferences.value = Preferences(name, connectable)
    }

    fun setPreferences(preferences: Preferences) {
        _preferences.value = preferences
    }

    fun setPreferences(name: String) {
        _preferences.value = Preferences(name, preferences.value.connectable)
    }

    fun setPreferences(connectable: Boolean) {
        _preferences.value = Preferences(preferences.value.name, connectable)
    }

    open fun savePreferences() {}

    fun nameIsInvalid(name: String) =
        name.isEmpty() || name.isBlank() || name.length > PreferencesDefaults.MAX_NAME_LENGTH
}
