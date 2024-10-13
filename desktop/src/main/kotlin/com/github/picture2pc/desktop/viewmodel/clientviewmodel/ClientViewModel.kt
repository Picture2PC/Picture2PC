package com.github.picture2pc.desktop.viewmodel.clientviewmodel

import com.github.picture2pc.desktop.data.clientpreferences.ClientPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientViewModel(
    private val repository: ClientPreferencesRepository,
    private val viewModelScope: CoroutineScope,
) {
    val clientName: StateFlow<String> get() = repository.clientName

    init {
        viewModelScope.launch {
            repository.loadClientName()
        }
    }

    fun saveClientName(name: String) {
        viewModelScope.launch {
            repository.setClientName(name)
        }
    }
}