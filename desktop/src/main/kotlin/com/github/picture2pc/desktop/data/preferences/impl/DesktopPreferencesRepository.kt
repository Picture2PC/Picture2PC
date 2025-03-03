package com.github.picture2pc.desktop.data.preferences.impl

import com.github.picture2pc.common.data.preferences.Preferences
import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
class DesktopPreferencesRepository : PreferencesRepository() {
    private val file = File("preferences.cbor")

    init {
        ensurePreferencesFile()
        setPreferences(Cbor.decodeFromByteArray<Preferences>(file.readBytes()))
    }

    override fun savePreferences() {
        val bytes = Cbor.encodeToByteArray(preferences.value)
        ensurePreferencesFile()
        file.writeBytes(bytes)
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
