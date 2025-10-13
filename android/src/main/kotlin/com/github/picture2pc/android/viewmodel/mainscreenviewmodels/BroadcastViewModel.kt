package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.picture2pc.android.data.serverpreferences.impl.AndroidPreferencesRepository
import kotlinx.coroutines.launch
import java.util.UUID

class BroadcastViewModel(private val preferencesRepository: AndroidPreferencesRepository) : ViewModel() {
    val name = preferencesRepository.name
    val connectable = preferencesRepository.connectable
    val groupName = preferencesRepository.groupName
    val groupUuid = preferencesRepository.groupUuid

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

    fun setGroupName(name: String) {
        viewModelScope.launch {
            preferencesRepository.setGroupName(name)
        }
    }

    fun createNewGroup(name: String) {
        viewModelScope.launch {
            val newGroupUuid = UUID.randomUUID().toString()
            preferencesRepository.setGroupUuid(newGroupUuid)
            preferencesRepository.setGroupName(name)
        }
    }

    fun nameIsInvalid(name: String) = preferencesRepository.nameIsInvalid(name)
}