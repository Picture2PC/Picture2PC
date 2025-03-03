package com.github.picture2pc.desktop.viewmodel.mainscreen

import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BroadcastViewModel(
    private val scope: CoroutineScope,
    private val preferences: PreferencesRepository
) {
    fun getName() = preferences.preferences.value.name
    fun getPreferences() = preferences.preferences

    fun setConnectable(connectable: Boolean) {
        scope.launch { preferences.setPreferences(connectable) }
    }

    fun saveName(newName: String) {
        scope.launch {
            preferences.setPreferences(newName, true)
            preferences.savePreferences()
        }
    }

    fun nameIsInvalid(name: String) = preferences.nameIsInvalid(name)
}