package org.picture2pc.picture2pc.domain.repository.group

import kotlinx.serialization.Serializable

@Serializable
data class Room(val name: String, val group: PublicGroup) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Room) return false
        if (name != other.name) return false
        if (group != other.group) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + group.hashCode()
        return result
    }
}