package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.picture2pc.android.data.serverpreferences.impl.AndroidPreferencesRepository
import kotlinx.coroutines.launch

class BroadcastViewModel(private val preferencesRepository: AndroidPreferencesRepository) : ViewModel() {
    val name = preferencesRepository.name
    val connectable = preferencesRepository.connectable

    fun setName(name: String) {
        viewModelScope.launch {
            preferencesRepository.setName(name)
        }
    }

    fun setConnectable(connectable: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setConnectable(connectable)
        }
    }

    fun nameIsInvalid(name: String) = preferencesRepository.nameIsInvalid(name)
}