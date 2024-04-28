package com.github.picture2pc.common.net2.payloads.impl

import com.github.picture2pc.common.net2.Peer
import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.serialization.Serializable

@Serializable
class ListPeers : Payload() {
    override val targetPeer: Peer = Peer.any()
}