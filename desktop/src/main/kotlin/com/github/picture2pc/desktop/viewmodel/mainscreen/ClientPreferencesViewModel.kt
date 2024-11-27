package com.github.picture2pc.desktop.viewmodel.mainscreen

import com.github.picture2pc.common.data.preferences.PreferencesRepository
import com.github.picture2pc.desktop.ui.constants.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ClientPreferencesViewModel(
    private val scope: CoroutineScope,
    private val preferences: PreferencesRepository
) {
    private val _serverName = MutableStateFlow(preferences.name.value)
    val name = _serverName.asStateFlow()

    private val _connectable = MutableStateFlow(preferences.connectable.value)
    val connectable = _connectable.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError = _isError.asStateFlow()


    init {
        preferences.name.onEach {
            _serverName.value = it
        }.launchIn(scope)

        preferences.connectable.onEach {
            _connectable.value = it
        }.launchIn(scope)
    }

    fun saveName(newName: String) {
        scope.launch {
            preferences.setName(newName)
        }
    }

    fun nameChanged(newName: String) {
        if (newName.length > Settings.MAX_NAME_LENGTH) return
        _serverName.value = newName
    }

    fun setError(isError: Boolean) {
        _isError.value = isError
    }
}