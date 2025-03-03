package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.coroutines.launch

class BroadcastViewModel(private val preferences: PreferencesRepository) : ViewModel() {
    fun getName() = preferences.preferences.value.name
    fun getPreferences() = preferences.preferences

    fun setConnectable(connectable: Boolean) {
        viewModelScope.launch { preferences.setPreferences(connectable) }
    }

    fun saveName(newName: String) {
        viewModelScope.launch { preferences.setPreferences(newName, true) }
    }

    fun nameIsInvalid(name: String) = preferences.nameIsInvalid(name)
}