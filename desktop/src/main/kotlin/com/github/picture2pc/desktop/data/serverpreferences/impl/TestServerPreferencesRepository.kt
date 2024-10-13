package com.github.picture2pc.desktop.data.serverpreferences.impl

import com.github.picture2pc.common.data.serverpreferences.ServerPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TestServerPreferencesRepository(initialName: String) : ServerPreferencesRepository() {
    private val _name = MutableStateFlow(initialName)
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