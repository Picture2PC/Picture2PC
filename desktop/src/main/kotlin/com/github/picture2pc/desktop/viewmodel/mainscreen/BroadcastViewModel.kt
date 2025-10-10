package com.github.picture2pc.desktop.viewmodel.mainscreen

import com.github.picture2pc.desktop.data.preferences.impl.DesktopPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BroadcastViewModel(
    private val scope: CoroutineScope,
    private val preferencesRepository: DesktopPreferencesRepository
) {
    fun getName() = preferencesRepository.name.value
    fun getConnectable() = preferencesRepository.connectable.value

    fun setConnectable(connectable: Boolean) {
        scope.launch {
            preferencesRepository.setConnectable(connectable)
            preferencesRepository.savePreferences()
        }
    }

    fun saveName(newName: String) {
        scope.launch {
            preferencesRepository.setName(newName)
            preferencesRepository.savePreferences()
        }
    }

    fun nameIsInvalid(name: String) = preferencesRepository.nameIsInvalid(name)
}