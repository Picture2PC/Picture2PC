package com.github.picture2pc.common.data.preferences

import kotlinx.coroutines.flow.StateFlow

abstract class PreferencesRepository {
    abstract val name: StateFlow<String>

    abstract val connectable: StateFlow<Boolean>

    abstract val deviceUuid: StateFlow<String>

    abstract val groupUuid: StateFlow<String>

    abstract val groupName: StateFlow<String>


    abstract suspend fun setConnectable(connectable: Boolean)

    abstract suspend fun setName(name: String)

    abstract suspend fun setDeviceUuid(uuid: String)

    abstract suspend fun setGroupUuid(uuid: String)

    abstract suspend fun setGroupName(name: String)

    fun nameIsInvalid(name: String) =
        name.isEmpty() || name.isBlank() || name.length > PreferencesDefaults.MAX_NAME_LENGTH
}
