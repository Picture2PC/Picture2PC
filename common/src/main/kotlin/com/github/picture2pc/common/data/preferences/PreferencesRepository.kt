package com.github.picture2pc.common.data.preferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
data class Preferences(
    var name: String = PreferencesDefaults.NAME,
    var connectable: Boolean = PreferencesDefaults.CONNECTABLE
)

abstract class PreferencesRepository {
    private val _name = MutableStateFlow(PreferencesDefaults.NAME)
    open val name: StateFlow<String> = _name

    private val _connectable = MutableStateFlow(PreferencesDefaults.CONNECTABLE)
    open val connectable: StateFlow<Boolean> = _connectable

    fun setPreferences(name: String, connectable: Boolean) {
        _name.value = name
        _connectable.value = connectable
    }

    open fun setConnectable(connectable: Boolean) {
        _connectable.value = connectable
    }

    open fun setName(name: String) {
        _name.value = name
    }

    fun buildPreferences(): Preferences {
        return Preferences(name.value, connectable.value)
    }

    fun nameIsInvalid(name: String) =
        name.isEmpty() || name.isBlank() || name.length > PreferencesDefaults.MAX_NAME_LENGTH
}
