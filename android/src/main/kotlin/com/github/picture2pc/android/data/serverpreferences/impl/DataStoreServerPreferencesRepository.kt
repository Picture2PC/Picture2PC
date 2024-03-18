package com.github.picture2pc.android.data.serverpreferences.impl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesDefaults
import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext


private object PreferenceKeys {
    val NAME = stringPreferencesKey("server_name")
    val CONNECTABLE = booleanPreferencesKey("server_connectable")
}

class DataStoreServerPreferencesRepository(
    private val context: Context,
    override val coroutineContext: CoroutineContext
) :
    CoroutineScope, ServerPreferencesRepository() {

    companion object {
        private val Context.settingsDataStore by preferencesDataStore(
            name = "settings"
        )
    }


    override val name: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[PreferenceKeys.NAME] ?: ServerPreferencesDefaults.NAME
    }

    override val connectable: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[PreferenceKeys.CONNECTABLE] ?: ServerPreferencesDefaults.CONNECTABLE
    }


    override suspend fun setName(name: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferenceKeys.NAME] = name
        }
    }

    override suspend fun setConnectable(connectable: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferenceKeys.CONNECTABLE] = connectable
        }
    }


}
