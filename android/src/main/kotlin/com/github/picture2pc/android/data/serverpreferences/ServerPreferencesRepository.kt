package com.github.picture2pc.android.data.serverpreferences

import kotlinx.coroutines.flow.Flow


abstract class ServerPreferencesRepository {
    abstract val name: Flow<String>
    abstract val connectable: Flow<Boolean>
    abstract suspend fun setName(name: String)
    abstract suspend fun setConnectable(connectable: Boolean)
}
