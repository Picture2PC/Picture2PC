package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.coroutines.launch

class BroadcastViewModel(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    val name = preferencesRepository.preferences.value.name
    val preferences = preferencesRepository.preferences

    fun setConnectable(connectable: Boolean) {
        viewModelScope.launch { preferencesRepository.setPreferences(connectable) }
    }

    fun saveName(newName: String) {
        viewModelScope.launch { preferencesRepository.setPreferences(newName, true) }
    }

    fun nameIsInvalid(name: String) = preferencesRepository.nameIsInvalid(name)
}