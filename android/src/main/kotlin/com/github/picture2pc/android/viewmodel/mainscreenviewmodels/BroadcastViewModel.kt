package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class BroadcastViewModel(
    private val serverSettingsRepository: ServerPreferencesRepository,
    override val coroutineContext: CoroutineContext
) :
    CoroutineScope {
    private val _name = serverSettingsRepository.name.stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = "Loading"
    )

    private val _connectable = serverSettingsRepository.connectable.stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    val name = MutableStateFlow(_name.value)
    val connectable = MutableStateFlow(_connectable.value)

    init {
        _name.onEach { name.value = it }.launchIn(this)
        _connectable.onEach { connectable.value = it }.launchIn(this)
    }

    fun checkedChanged(newConnectable: Boolean) {
        launch { serverSettingsRepository.setConnectable(newConnectable) }
    }

    fun nameChanged(newName: String) {
        launch { serverSettingsRepository.setName(newName) }
    }
}