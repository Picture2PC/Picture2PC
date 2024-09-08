package com.github.picture2pc.common.net.data.peer

import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import java.util.UUID

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
            return Peer("0", true)
        }

        private val uuid: String = UUID.randomUUID().toString()


        fun getSelf(): Peer {
            return Peer(uuid, false)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Peer) return false

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}

