package com.github.picture2pc.common.net.data.payload

import com.github.picture2pc.common.net.data.peer.Peer
import kotlinx.serialization.Serializable
import java.net.InetSocketAddress

@Serializable
sealed class MulticastPayload : Payload() {
    @Serializable
    data class PeerTcpOnline(val port: Int, val clientName: String, override val targetPeer: Peer) :
        MulticastPayload() {
        val tcpServerSocketAddress: InetSocketAddress
            get() = InetSocketAddress(receivedPayloadInfo?.senderInetSocketAddress?.address, port)
    }

    @Serializable
    data class ListPeers(val clientName: String) : MulticastPayload() {
        override val targetPeer: Peer = Peer.any()
    }
}
