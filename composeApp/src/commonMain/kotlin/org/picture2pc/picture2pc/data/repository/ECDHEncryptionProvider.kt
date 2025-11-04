package org.picture2pc.picture2pc.data.repository

import dev.whyoleg.cryptography.BinarySize.Companion.bytes
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.*
import dev.whyoleg.cryptography.algorithms.AES.Key
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.picture2pc.picture2pc.data.repository.net.peer.Peer
import org.picture2pc.picture2pc.domain.repository.EncryptionProvider
import org.picture2pc.picture2pc.domain.repository.PreferencesRepository
import org.picture2pc.picture2pc.domain.repository.SharedKey

class AESSharedKey(private val cipher: AES.IvAuthenticatedCipher) : SharedKey {
    override suspend fun encrypt(data: ByteArray): ByteArray = cipher.encrypt(data)
    override suspend fun decrypt(data: ByteArray): ByteArray? = runCatching { cipher.decrypt(data) }.getOrNull()
}

class ECDHEncryptionProvider(
    private val preferencesRepository: PreferencesRepository,
    private val backgroundScope: CoroutineScope
) : EncryptionProvider {
    private val ecdhProvider = CryptographyProvider.Default.get(ECDH)
    private val hkdfProvider = CryptographyProvider.Default.get(HKDF)
    private val aesProvider = CryptographyProvider.Default.get(AES.GCM)

    private var privateKey: ECDH.PrivateKey? = null
    private var privateKeyString: String = ""

    var publicKey: ECDH.PublicKey? = null
        private set
    override var publicKeyString: String = ""
        private set

    init {
        preferencesRepository.privateKey.onEach {
            this.privateKeyString = it
            privateKey = runCatching {
                ecdhProvider.privateKeyDecoder(
                    EC.Curve.P521
                ).decodeFromByteArray(EC.PrivateKey.Format.DER, it.decodeBase64Bytes())
            }.getOrNull()
            if (privateKey == null) {
                generateKeyPair()
            }
        }.launchIn(backgroundScope)
        preferencesRepository.publicKey.onEach {
            this.publicKeyString = it
            publicKey = runCatching {
                ecdhProvider.publicKeyDecoder(
                    EC.Curve.P521
                ).decodeFromByteArray(EC.PublicKey.Format.DER, it.decodeBase64Bytes())
            }.getOrNull()
        }.launchIn(backgroundScope)
    }

    override suspend fun getSharedKey(peer: Peer): AESSharedKey? {
        val secret = privateKey?.sharedSecretGenerator()?.generateSharedSecret(
            ecdhProvider.publicKeyDecoder(EC.Curve.P521)
                .decodeFromByteArray(EC.PublicKey.Format.DER, peer.uuid.decodeBase64Bytes())
        )
        secret ?: return null
        val aesData = hkdfProvider.secretDerivation(
            digest = SHA512,
            outputSize = 32.bytes,
            salt = "".encodeToByteArray(),
            "picture2pc-peer-${peer.uuid.slice(0..10)}".encodeToByteArray()
        ).deriveSecret(secret)
        val aesCipher = aesProvider.keyDecoder().decodeFromByteString(Key.Format.RAW, aesData).cipher()
        return AESSharedKey(aesCipher)
    }

    private suspend fun generateKeyPair() {
        val keyPair = ecdhProvider.keyPairGenerator(EC.Curve.P521).generateKey()
        preferencesRepository.setPublicKey(keyPair.publicKey.encodeToByteArray(EC.PublicKey.Format.DER).encodeBase64())
        preferencesRepository.setPrivateKey(
            keyPair.privateKey.encodeToByteArray(EC.PrivateKey.Format.DER).encodeBase64()
        )
    }
}