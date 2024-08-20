package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class BroadcastViewModel(
    private val serverPreferencesRepository: ServerPreferencesRepository,
    override val coroutineContext: CoroutineContext
) :
    CoroutineScope {
    private val _serverNamePreference = serverPreferencesRepository.name.stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = "<LOADING>"
    )

    val serverConnectable = serverPreferencesRepository.connectable.stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    // This is because writing needs a buffer, so
    // that it only saves when *Done* is pressed
    private val _serverName = MutableStateFlow(_serverNamePreference.value)
    val serverName = _serverName.asStateFlow()

    init {
        _serverNamePreference.onEach { _serverName.value = it }.launchIn(this)
    }

    fun saveConnectable(newConnectable: Boolean) {
        launch { serverPreferencesRepository.setConnectable(newConnectable) }
    }

    fun saveName(newName: String) {
        launch { serverPreferencesRepository.setName(newName) }
    }

    fun nameChanged(newName: String) {
        _serverName.value = newName
    }


}