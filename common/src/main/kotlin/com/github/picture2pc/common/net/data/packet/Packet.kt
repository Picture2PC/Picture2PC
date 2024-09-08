package com.github.picture2pc.common.net.data.packet

import com.github.picture2pc.common.net.data.peer.Peer
import kotlinx.serialization.Serializable

@Serializable
data class Packet(val type: String, val len: Int, val sourcePeer: Peer)