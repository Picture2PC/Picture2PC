package org.picture2pc.picture2pc.domain.repository.net.encryption

import kotlinx.coroutines.flow.StateFlow
import org.picture2pc.picture2pc.data.repository.net.common.peer.Peer

interface EncryptionProvider {
    suspend fun getSharedKey(peer: Peer): SharedKey?
    suspend fun hashString(data: String): String
    val publicKeyString: StateFlow<String>
}