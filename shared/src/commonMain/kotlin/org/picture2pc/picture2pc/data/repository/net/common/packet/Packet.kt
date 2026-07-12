package org.picture2pc.picture2pc.data.repository.net.common.packet

import kotlinx.serialization.Serializable
import org.picture2pc.picture2pc.data.repository.net.common.peer.Peer

@Serializable
data class Packet(
    val type: String,
    val len: Int,
    val sourcePeer: Peer,
    val encrypted: Boolean
) {
    companion object
}