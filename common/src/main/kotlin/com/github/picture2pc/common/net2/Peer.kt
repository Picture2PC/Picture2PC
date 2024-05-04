package com.github.picture2pc.common.net2

import kotlinx.serialization.Serializable

@Serializable
class Peer {
    val name: String
    private val uuid: String
    val isAny: Boolean

    constructor(name: String, uuid: String, isAny: Boolean) {
        this.name = name
        this.uuid = uuid
        this.isAny = isAny
    }

    companion object {
        fun any(): Peer {
            return Peer("Any", "0", true)
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

