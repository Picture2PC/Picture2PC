package com.github.picture2pc.android.data.Device

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Device (private val context: Context, override val coroutineContext: CoroutineContext) :CoroutineScope{

    companion object {
        private val Context.dataStore by preferencesDataStore(
            name = "device_settings"
        )
        val DEVICE_NAME_KEY = stringPreferencesKey("device_name")
        val DEVICE_SERVING_KEY = booleanPreferencesKey("device_serving")
    }
    val name :MutableStateFlow<String> = MutableStateFlow<String>("")
    val serving : MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(false)

    init {
        launch {
            name.emit(_name.first())
            serving.emit(_serving.first())
        }
        name.onEach { s -> setName(s) }.launchIn(this)
        serving.onEach { s-> setServing(s) }.launchIn(this)
        }



    private val _name: Flow<String>
        get(){return context.dataStore.data
            .map { preferences ->
                preferences[DEVICE_NAME_KEY] ?: ""
            }
        }

    private suspend fun setName(name:String){
        context.dataStore.edit { preferences ->
            preferences[DEVICE_NAME_KEY] =name
        }
    }
    private val _serving: Flow<Boolean>
        get(){return context.dataStore.data
            .map { preferences ->
                preferences[DEVICE_SERVING_KEY] ?: false
            }
        }

    private suspend fun setServing(name:Boolean){
        context.dataStore.edit { preferences ->
            preferences[DEVICE_SERVING_KEY] =name
        }
    }


}
