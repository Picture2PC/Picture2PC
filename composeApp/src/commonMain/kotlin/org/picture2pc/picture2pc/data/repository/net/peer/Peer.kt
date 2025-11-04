package org.picture2pc.picture2pc.data.repository.net.peer

import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.picture2pc.picture2pc.domain.repository.EncryptionProvider

@Serializable
open class Peer {
    val uuid: String
    val isAny: Boolean

    constructor(uuid: String, isAny: Boolean) {
        this.uuid = uuid
        this.isAny = isAny
    }

    companion object : KoinComponent {
        fun any(): Peer {
            return Peer("", true)
        }

        private val encryptionProvider: EncryptionProvider = get()

        fun getSelf(): Peer {
            return Peer(encryptionProvider.publicKeyString, encryptionProvider.publicKeyString == "")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Peer) return false

        if (uuid == other.uuid) return true
        return false
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}

