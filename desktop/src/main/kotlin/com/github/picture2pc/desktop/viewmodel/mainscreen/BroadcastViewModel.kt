package com.github.picture2pc.desktop.viewmodel.mainscreen

import com.github.picture2pc.desktop.data.preferences.impl.DesktopPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

class BroadcastViewModel(
    private val scope: CoroutineScope,
    private val preferencesRepository: DesktopPreferencesRepository
) {
    fun getName() = preferencesRepository.name.value
    fun getConnectable() = preferencesRepository.connectable.value
    fun getGroupName() = preferencesRepository.groupName.value
    fun getGroupUuid() = preferencesRepository.groupUuid.value

    fun setConnectable(connectable: Boolean) {
        scope.launch {
            preferencesRepository.setConnectable(connectable)
        }
    }

    fun saveName(newName: String) {
        scope.launch {
            preferencesRepository.setName(newName)
        }
    }

    fun setGroupName(name: String) {
        scope.launch {
            preferencesRepository.setGroupName(name)
        }
    }

    fun createNewGroup(name: String) {
        scope.launch {
            val newGroupUuid = UUID.randomUUID().toString()
            preferencesRepository.setGroupUuid(newGroupUuid)
            preferencesRepository.setGroupName(name)
        }
    }

    fun nameIsInvalid(name: String) = preferencesRepository.nameIsInvalid(name)
}