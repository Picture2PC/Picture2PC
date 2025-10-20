package org.picture2pc.picture2pc.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface PreferencesRepository {

    val name: StateFlow<String>
    val connectable: StateFlow<Boolean>

    suspend fun setName(name: String)
    suspend fun setConnectable(connectable: Boolean)
}
