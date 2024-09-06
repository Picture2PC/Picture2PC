package com.github.picture2pc.common.net.payloads

import com.github.picture2pc.common.net.Peer
import kotlinx.serialization.Serializable
import java.net.InetSocketAddress

@Serializable
sealed class MulticastPayload : Payload() {
    @Serializable
    data class PeerTcpOnline(val port: Int, override val targetPeer: Peer) : MulticastPayload() {
        val tcpServerSocketAddress: InetSocketAddress
            get() = InetSocketAddress(receivedFromInetSocketAddress?.address, port)
    }

    @Serializable
    class ListPeers : MulticastPayload() {
        override val targetPeer: Peer = Peer.any()
    }
}
