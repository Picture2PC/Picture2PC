package org.picture2pc.picture2pc.data.repository.net.packet

import kotlinx.serialization.Serializable
import org.picture2pc.picture2pc.data.repository.net.peer.Peer

@Serializable
data class Packet(val type: String, val len: Int, val sourcePeer: Peer, val encrypted: Boolean)