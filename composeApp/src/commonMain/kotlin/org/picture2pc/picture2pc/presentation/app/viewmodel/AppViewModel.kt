package org.picture2pc.picture2pc.presentation.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.picture2pc.picture2pc.domain.repository.net.DataTransmitter
import org.picture2pc.picture2pc.domain.usecase.PreferencesUseCase
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class AppViewModel(
    private val preferencesUseCase: PreferencesUseCase,
    private val clientDiscovery: DataTransmitter
) : ViewModel() {
    val name = preferencesUseCase.name
    val connectable = preferencesUseCase.connectable

    init {
        clientDiscovery.connectedClients.onEach {
            println(it)
            it.forEach { c ->
                c.name.onEach { println("${c.name.value} ${c.securityState.value} ${c.state.value}") }
                    .launchIn(viewModelScope)
                //c.state.onEach { println("${c.name.value} ${c.securityState.value} ${c.state.value}") }.launchIn(viewModelScope)
                c.securityState.onEach { println("${c.name.value} ${c.securityState.value} ${c.state.value}") }
                    .launchIn(viewModelScope)
            }
        }.launchIn(viewModelScope)
    }

    fun setName(name: String) {
        viewModelScope.launch { preferencesUseCase.setName(name) }
    }

    fun setConnectable(connectable: Boolean) {
        viewModelScope.launch { preferencesUseCase.setConnectable(connectable) }
    }
}