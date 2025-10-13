package com.github.picture2pc.common.data.preferences

import kotlinx.coroutines.flow.StateFlow

abstract class PreferencesRepository {
    abstract val name: StateFlow<String>

    abstract val connectable: StateFlow<Boolean>


    abstract suspend fun setConnectable(connectable: Boolean)

    abstract suspend fun setName(name: String)

    fun nameIsInvalid(name: String) =
        name.isEmpty() || name.isBlank() || name.length > PreferencesDefaults.MAX_NAME_LENGTH
}
