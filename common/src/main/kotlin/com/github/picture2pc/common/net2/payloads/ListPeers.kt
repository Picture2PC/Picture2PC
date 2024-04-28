package com.github.picture2pc.common.net2.payloads

import com.github.picture2pc.common.net2.Peer
import kotlinx.serialization.Serializable

@Serializable
class ListPeers : Payload() {
    override val targetPeer: Peer = Peer.any()
}