package com.github.picture2pc.desktop.data.preferences.impl

import com.github.picture2pc.common.data.preferences.Preferences
import com.github.picture2pc.common.data.preferences.PreferencesRepository
import com.github.picture2pc.common.net.data.peer.Peer.Companion.getKoin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import org.koin.core.qualifier.named
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
class DesktopPreferencesRepository(
    private val backgroundCoroutineScope: CoroutineScope = getKoin().get(named("backgroundCoroutineScope")),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PreferencesRepository() {
    private val file = File("preferences.cbor")

    init {
        backgroundCoroutineScope.launch(ioDispatcher) {
            file.createNewFile()
        }
        loadPreferences()
    }

    fun savePreferences() {
        backgroundCoroutineScope.launch(ioDispatcher) {
            val bytes = Cbor.encodeToByteArray(buildPreferences())
            file.createNewFile()
            file.writeBytes(bytes)
        }
    }

    fun loadPreferences() {
        val preferences = try {
            Cbor.decodeFromByteArray<Preferences>(file.readBytes())
        } catch (_: Exception) {
            Preferences()
        }
        setName(preferences.name)
        setConnectable(preferences.connectable)
    }
}
