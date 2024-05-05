package com.github.picture2pc.common.net2.payloads

import com.github.picture2pc.common.net2.Peer
import kotlinx.serialization.Serializable

@Serializable
sealed class TcpPayload : Payload() {
    @Serializable
    class Ping(override val targetPeer: Peer) : TcpPayload()

    @Serializable
    data class Pong(override val targetPeer: Peer) : TcpPayload()

    @Serializable
    data class Picture(val picture: String, override val targetPeer: Peer = Peer.any()) :
        TcpPayload()
}