package org.picture2pc.picture2pc.presentation.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.picture2pc.picture2pc.data.repository.net.payload.DiscoverPayload
import org.picture2pc.picture2pc.data.repository.net.peer.Peer
import org.picture2pc.picture2pc.domain.repository.net.ClientDiscovery
import org.picture2pc.picture2pc.domain.usecase.PreferencesUseCase
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class AppViewModel(
    private val preferencesUseCase: PreferencesUseCase,
    private val clientDiscovery: ClientDiscovery
) : ViewModel() {
    val name = preferencesUseCase.name
    val connectable = preferencesUseCase.connectable

    init {
        clientDiscovery.discover(DiscoverPayload.ServiceOnline(1000, "HALLO", Peer.any())).onEach {
            println(it)
        }.launchIn(viewModelScope)
    }

    fun setName(name: String) {
        viewModelScope.launch { preferencesUseCase.setName(name) }
    }

    fun setConnectable(connectable: Boolean) {
        viewModelScope.launch { preferencesUseCase.setConnectable(connectable) }
    }
}