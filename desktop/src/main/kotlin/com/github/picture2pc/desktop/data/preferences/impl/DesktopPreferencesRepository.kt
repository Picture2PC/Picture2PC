package com.github.picture2pc.desktop.data.preferences.impl

import com.github.picture2pc.common.data.preferences.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DesktopPreferencesRepository : PreferencesRepository() {
    private val _name = MutableStateFlow("test")
    override val name: StateFlow<String> = _name

    private val _connectable = MutableStateFlow(true)
    override val connectable: StateFlow<Boolean> = _connectable

    override suspend fun setName(name: String) {
        _name.value = name
    }

    override suspend fun setConnectable(connectable: Boolean) {
        _connectable.value = connectable
    }
}
