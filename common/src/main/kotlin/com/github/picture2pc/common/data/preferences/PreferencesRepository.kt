package com.github.picture2pc.common.data.preferences

import kotlinx.coroutines.flow.StateFlow

abstract class PreferencesRepository {
    //val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    abstract val name: StateFlow<String>
    abstract val connectable: StateFlow<Boolean>
    abstract suspend fun setName(name: String)
    abstract suspend fun setConnectable(connectable: Boolean)
}
