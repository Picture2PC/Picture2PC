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
    var connectable: Boolean = PreferencesDefaults.CONNECTABLE,
    var deviceUuid: String = PreferencesDefaults.DEVICE_UUID,
    var groupUuid: String = PreferencesDefaults.GROUP_UUID,
    var groupName: String = PreferencesDefaults.GROUP_NAME
)

class DesktopPreferencesRepository(
    private val backgroundCoroutineScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
) : PreferencesRepository() {
    private val _name = MutableStateFlow(PreferencesDefaults.NAME)
    override val name: StateFlow<String> = _name.asStateFlow()
    private val _connectable = MutableStateFlow(PreferencesDefaults.CONNECTABLE)
    override val connectable: StateFlow<Boolean> = _connectable.asStateFlow()
    private val _deviceUuid = MutableStateFlow(PreferencesDefaults.DEVICE_UUID)
    override val deviceUuid: StateFlow<String> = _deviceUuid.asStateFlow()
    private val _groupUuid = MutableStateFlow(PreferencesDefaults.GROUP_UUID)
    override val groupUuid: StateFlow<String> = _groupUuid.asStateFlow()
    private val _groupName = MutableStateFlow(PreferencesDefaults.GROUP_NAME)
    override val groupName: StateFlow<String> = _groupName.asStateFlow()

    override suspend fun setName(name: String) {
        _name.value = name
        savePreferences()
    }

    override suspend fun setConnectable(connectable: Boolean) {
        _connectable.value = connectable
        savePreferences()
    }

    override suspend fun setDeviceUuid(uuid: String) {
        _deviceUuid.value = uuid
        savePreferences()
    }

    override suspend fun setGroupUuid(uuid: String) {
        _groupUuid.value = uuid
        savePreferences()
    }

    override suspend fun setGroupName(name: String) {
        _groupName.value = name
        savePreferences()
    }

    private val file = File("preferences.cbor")

    init {
        backgroundCoroutineScope.launch {
            loadPreferences()
        }
    }

    private fun buildPreferences(): Preferences {
        return Preferences(name.value, connectable.value, deviceUuid.value, groupUuid.value, groupName.value)
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
            _deviceUuid.value = preferences.deviceUuid
            _groupUuid.value = preferences.groupUuid
            _groupName.value = preferences.groupName
        }
    }
}
