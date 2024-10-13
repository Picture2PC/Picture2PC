package com.github.picture2pc.android.data.serverpreferences.impl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesDefaults
import com.github.picture2pc.common.data.serverpreferences.ServerPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext


private object PreferenceKeys {
    val NAME = stringPreferencesKey("server_name")
    val CONNECTABLE = booleanPreferencesKey("server_connectable")
}

class DataStoreServerPreferencesRepository(
    private val context: Context,
    backgroundScope: CoroutineScope,
) : ServerPreferencesRepository() {

    companion object {
        private val Context.settingsDataStore by preferencesDataStore(
            name = "settings"
        )
    }

    override val name = context.settingsDataStore.data.map { preferences ->
        preferences[PreferenceKeys.NAME] ?: ServerPreferencesDefaults.NAME
    }.stateIn(
        scope = backgroundScope,
        started = SharingStarted.Eagerly,
        initialValue = "<LOADING>"
    )

    override val connectable = context.settingsDataStore.data.map { preferences ->
        preferences[PreferenceKeys.CONNECTABLE] ?: ServerPreferencesDefaults.CONNECTABLE
    }.stateIn(
        scope = backgroundScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )


    override suspend fun setName(name: String) {
        withContext(Dispatchers.IO) {
            context.settingsDataStore.edit { preferences ->
                preferences[PreferenceKeys.NAME] = name
            }
        }
    }

    override suspend fun setConnectable(connectable: Boolean) {
        withContext(Dispatchers.IO) {
            context.settingsDataStore.edit { preferences ->
                preferences[PreferenceKeys.CONNECTABLE] = connectable
            }
        }
    }
}
