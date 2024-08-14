package com.github.picture2pc.common.net2.payloads

import com.github.picture2pc.common.net2.Peer
import kotlinx.serialization.Serializable
import java.net.InetSocketAddress

@Serializable
sealed class MulticastPayload : Payload() {
    // ------------Internal--------------
    @Serializable
    data class Ping(override val targetPeer: Peer) : TcpPayload()

    @Serializable
    data class Pong(override val targetPeer: Peer) : TcpPayload()

    // ------------Internal--------------

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
