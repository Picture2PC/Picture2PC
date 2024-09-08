package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class BroadcastViewModel(private val serverPreferences: ServerPreferencesRepository) : ViewModel() {
    // This is because writing needs a buffer, so
    // that it only saves when *Done* is pressed
    private val _serverName = MutableStateFlow(serverPreferences.name.value)
    val serverName = _serverName.asStateFlow()

    private val _connectable = MutableStateFlow(serverPreferences.connectable.value)
    val connectable = _connectable.asStateFlow()

    init {
        serverPreferences.name.onEach {
            _serverName.value = it
        }.launchIn(viewModelScope)

        serverPreferences.connectable.onEach {
            _connectable.value = it
        }.launchIn(viewModelScope)
    }

    fun saveConnectable(newConnectable: Boolean) {
        viewModelScope.launch { serverPreferences.setConnectable(newConnectable) }
    }

    fun saveName(newName: String) {
        viewModelScope.launch { serverPreferences.setName(newName) }
    }

    fun nameChanged(newName: String) {
        _serverName.value = newName
    }
}