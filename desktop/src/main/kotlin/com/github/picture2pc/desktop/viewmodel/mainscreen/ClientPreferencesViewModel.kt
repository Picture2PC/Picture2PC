package com.github.picture2pc.desktop.viewmodel.mainscreen

import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ClientPreferencesViewModel(
    private val scope: CoroutineScope,
    private val preferences: PreferencesRepository
) {
    fun setConnectable(connectable: Boolean) {
        scope.launch { preferences.setConnectable(connectable) }
    }

    fun getName() = preferences.name.value

    fun saveName(newName: String) {
        scope.launch {
            preferences.setName(newName)
            preferences.setConnectable(true)
        }
    }
}