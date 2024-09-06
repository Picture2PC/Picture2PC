package com.github.picture2pc.common.net.data.payload

import com.github.picture2pc.common.net.data.peer.Peer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class Payload {
    abstract val targetPeer: Peer
    val sourcePeer: Peer = Peer.getSelf()


    // will be set by the transceiver
    @Transient
    var receivedPayloadInfo: PayloadInfo? = null
}