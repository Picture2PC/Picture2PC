package com.github.picture2pc.common.data.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.StateFlow
import java.util.prefs.Preferences

abstract class PreferencesRepository {
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    abstract val name: StateFlow<String>
    abstract val connectable: StateFlow<Boolean>
    abstract suspend fun setName(name: String)
    abstract suspend fun setConnectable(connectable: Boolean)
}
