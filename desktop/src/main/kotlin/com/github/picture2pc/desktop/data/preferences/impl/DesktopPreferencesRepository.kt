package com.github.picture2pc.desktop.data.preferences.impl

import com.github.picture2pc.common.data.preferences.PreferencesDefaults
import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import java.io.File

@Serializable
data class Preferences(
    var name: String = PreferencesDefaults.NAME,
    var connectable: Boolean = PreferencesDefaults.CONNECTABLE
)

class DesktopPreferencesRepository(
    private val backgroundCoroutineScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
) : PreferencesRepository() {
    private val _name = MutableStateFlow(PreferencesDefaults.NAME)
    override val name: StateFlow<String> = _name.asStateFlow()
    private val _connectable = MutableStateFlow(PreferencesDefaults.CONNECTABLE)
    override val connectable: StateFlow<Boolean> = _connectable.asStateFlow()

    override suspend fun setName(name: String) {
        _name.value = name
        savePreferences()
    }

    override suspend fun setConnectable(connectable: Boolean) {
        _connectable.value = connectable
        savePreferences()
    }

    private val file = File("preferences.cbor")

    init {
        backgroundCoroutineScope.launch {
            loadPreferences()
        }
    }

    private fun buildPreferences(): Preferences {
        return Preferences(name.value, connectable.value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun savePreferences() {
        backgroundCoroutineScope.launch(ioDispatcher) {
            val bytes = Cbor.encodeToByteArray<Preferences>(buildPreferences())
            file.writeBytes(bytes)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun loadPreferences() {
        withContext(ioDispatcher) {
            file.createNewFile()
            val preferences = try {
                Cbor.decodeFromByteArray<Preferences>(file.readBytes())
            } catch (_: Exception) {
                Preferences()
            }
            _name.value = preferences.name
            _connectable.value = preferences.connectable
        }
    }
}
