package com.github.picture2pc.android.data.serverpreferences.impl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.picture2pc.common.data.preferences.PreferencesDefaults
import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AndroidPreferencesRepository(
    private val context: Context,
    private val backgroundScope: CoroutineScope,
) : PreferencesRepository() {

    private val nameKey = stringPreferencesKey("server_name")
    private val connectableKey = booleanPreferencesKey("server_connectable")
    companion object {
        private val Context.settingsDataStore by preferencesDataStore(name = "settings")
    }

    override val name = context.settingsDataStore.data.map { preferences ->
        preferences[nameKey] ?: PreferencesDefaults.NAME
    }.stateIn(
        scope = backgroundScope,
        started = SharingStarted.Eagerly,
        initialValue = "<LOADING>"
    )

    override val connectable = context.settingsDataStore.data.map { preferences ->
        preferences[connectableKey] ?: PreferencesDefaults.CONNECTABLE
    }.stateIn(
        scope = backgroundScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    override fun setName(name: String) {
        super.setName(name)
        backgroundScope.launch(Dispatchers.IO) {
            context.settingsDataStore.edit { storedPreferences ->
                storedPreferences[nameKey] = name.trim()
            }
        }
    }

    override fun setConnectable(connectable: Boolean) {
        super.setConnectable(connectable)
        backgroundScope.launch(Dispatchers.IO) {
            context.settingsDataStore.edit { storedPreferences ->
                storedPreferences[connectableKey] = connectable
            }
        }
    }

    fun savePreferences() {
        backgroundScope.launch(Dispatchers.IO) {
            context.settingsDataStore.edit { storedPreferences ->
                storedPreferences[connectableKey] = connectable.value
                storedPreferences[nameKey] = name.value.trim()
            }
        }
    }
}
