package org.picture2pc.picture2pc.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface PreferencesRepository {

    val name: StateFlow<String>
    val connectable: StateFlow<Boolean>
    val privateKey: StateFlow<String>
    val publicKey: StateFlow<String>
    val groups: StateFlow<String>

    suspend fun setName(name: String)
    suspend fun setConnectable(connectable: Boolean)

    suspend fun setPrivateKey(privateKey: String)
    suspend fun setPublicKey(publicKey: String)
    suspend fun setGroupData(groups: String)
}
