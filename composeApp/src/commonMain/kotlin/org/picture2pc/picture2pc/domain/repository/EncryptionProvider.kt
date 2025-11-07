package org.picture2pc.picture2pc.domain.repository

import org.picture2pc.picture2pc.data.repository.net.peer.Peer

interface SharedKey {
    suspend fun encrypt(data: ByteArray): ByteArray
    suspend fun decrypt(data: ByteArray): ByteArray?
}

interface EncryptionProvider {
    suspend fun getSharedKey(peer: Peer): SharedKey?
    val publicKeyString: String
}