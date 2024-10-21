package com.github.picture2pc.desktop.viewmodel.clientviewmodel

import com.github.picture2pc.common.data.serverpreferences.PreferencesDataClass
import com.github.picture2pc.desktop.data.clientpreferences.ClientPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientViewModel(
    private val repository: ClientPreferencesRepository,
    val viewModelScope: CoroutineScope,
) {
    val preferences: StateFlow<PreferencesDataClass> get() = repository.preferences

    init {
        viewModelScope.launch { repository.loadPreferences() }
    }

    fun saveClientName(name: String) {
        viewModelScope.launch {
            repository.preferences.value.name = name
            repository.savePreferences()
        }.invokeOnCompletion { println(repository.preferences.value.name) }
    }
}