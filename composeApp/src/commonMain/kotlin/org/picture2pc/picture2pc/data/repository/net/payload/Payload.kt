package org.picture2pc.picture2pc.data.repository.net.payload

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.picture2pc.picture2pc.data.repository.net.peer.Peer

@Serializable
sealed class Payload {
    abstract val targetPeer: Peer
    val sourcePeer: Peer = Peer.getSelf()


    // will be set by the transceiver
    @Transient
    var receivedPayloadInfo: PayloadInfo? = null
}