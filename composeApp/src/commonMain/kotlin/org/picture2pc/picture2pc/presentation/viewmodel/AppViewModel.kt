package org.picture2pc.picture2pc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.picture2pc.picture2pc.domain.usecase.PreferencesUseCase

class AppViewModel(private val preferencesUseCase: PreferencesUseCase) : ViewModel() {
    val name = preferencesUseCase.name
    val connectable = preferencesUseCase.connectable

    fun setName(name: String) {
        viewModelScope.launch { preferencesUseCase.setName(name) }
    }

    fun setConnectable(connectable: Boolean) {
        viewModelScope.launch { preferencesUseCase.setConnectable(connectable) }
    }
}