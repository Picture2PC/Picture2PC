package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.coroutines.launch

class BroadcastViewModel(private val preferences: PreferencesRepository) : ViewModel()
{
    val connectable = preferences.connectable
    val name = preferences.name

    fun setConnectable(connectable: Boolean) {
        viewModelScope.launch { preferences.setConnectable(connectable) }
    }

    fun getName() = preferences.name.value

    fun saveName(newName: String) {
        viewModelScope.launch {
            preferences.setName(newName)
            preferences.setConnectable(true)
        }
    }

    fun nameIsInvalid(name: String) = preferences.nameIsInvalid(name)
}