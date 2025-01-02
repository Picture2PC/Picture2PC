package com.github.picture2pc.common.data.preferences

import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
data class Preferences(var name: String, var connectable: Boolean)

abstract class PreferencesRepository {
    abstract val name: StateFlow<String>
    abstract val connectable: StateFlow<Boolean>

    var preferences: Preferences =
        Preferences(PreferencesDefaults.NAME, PreferencesDefaults.CONNECTABLE)

    abstract suspend fun setName(name: String)
    abstract suspend fun setConnectable(connectable: Boolean)
    abstract fun savePreferences()
    abstract fun loadPreferences(): Preferences

    fun nameIsInvalid(name: String) = name.isEmpty() || name.isBlank() || name.length > PreferencesDefaults.MAX_NAME_LENGTH
}
