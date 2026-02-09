package org.picture2pc.picture2pc.data.repository.net.encryption

import dev.whyoleg.cryptography.algorithms.AES
import org.picture2pc.picture2pc.domain.repository.net.encryption.SharedKey

class AESSharedKey(private val cipher: AES.IvAuthenticatedCipher) : SharedKey {
    override suspend fun encrypt(data: ByteArray): ByteArray = cipher.encrypt(data)
    override suspend fun decrypt(data: ByteArray): ByteArray? = runCatching { cipher.decrypt(data) }.getOrNull()
}