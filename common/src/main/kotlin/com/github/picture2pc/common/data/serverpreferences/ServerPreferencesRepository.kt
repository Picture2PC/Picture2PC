package com.github.picture2pc.common.data.serverpreferences

import kotlinx.coroutines.flow.StateFlow

abstract class ServerPreferencesRepository {
    abstract val name: StateFlow<String>
    abstract val connectable: StateFlow<Boolean>
    abstract suspend fun setName(name: String)
    abstract suspend fun setConnectable(connectable: Boolean)
}