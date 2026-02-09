package org.picture2pc.picture2pc.domain.repository.net.encryption

interface SharedKey {
    suspend fun encrypt(data: ByteArray): ByteArray
    suspend fun decrypt(data: ByteArray): ByteArray?
}