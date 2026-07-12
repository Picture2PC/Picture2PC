package org.picture2pc.picture2pc.domain.repository.group

import kotlinx.serialization.Serializable
import kotlin.jvm.Transient


class InternalGroup(name: String, hashedUuid: String, val uuid: String, @Transient var combinedUuid: String) :
    PublicGroup(name, hashedUuid)

@Serializable
open class PublicGroup(val name: String, val hashedUuid: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PublicGroup) return false
        if (hashedUuid != other.hashedUuid) return false
        return true
    }

    override fun hashCode(): Int {
        return hashedUuid.hashCode()
    }
}