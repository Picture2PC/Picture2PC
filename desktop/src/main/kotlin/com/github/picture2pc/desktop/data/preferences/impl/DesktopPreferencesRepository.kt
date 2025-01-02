package com.github.picture2pc.desktop.data.preferences.impl

import com.github.picture2pc.common.data.preferences.Preferences
import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
class DesktopPreferencesRepository : PreferencesRepository() {
    private val _name = MutableStateFlow("test")
    override val name: StateFlow<String> = _name

    private val _connectable = MutableStateFlow(true)
    override val connectable: StateFlow<Boolean> = _connectable

    private val file = File("preferences.cbor")

    override suspend fun setName(name: String) {
        _name.value = name
        preferences.name = name
        savePreferences()
    }

    override suspend fun setConnectable(connectable: Boolean) {
        _connectable.value = connectable
        preferences.connectable = connectable
        savePreferences()
    }

    override fun savePreferences() {
        val bytes = Cbor.encodeToByteArray(preferences)
        ensurePreferencesFile()
        file.writeBytes(bytes)
    }

    override fun loadPreferences(): Preferences {
        ensurePreferencesFile()
        preferences = Cbor.decodeFromByteArray<Preferences>(file.readBytes())
        _name.value = preferences.name
        _connectable.value = preferences.connectable
        return preferences
    }

    /**
     * Ensures that the preferences file exists and is not empty.
     * @return true if the file is not empty, false otherwise.
     */
    private fun ensurePreferencesFile() {
        if (!file.exists()) {
            file.createNewFile()
            savePreferences()
        }
    }
}
