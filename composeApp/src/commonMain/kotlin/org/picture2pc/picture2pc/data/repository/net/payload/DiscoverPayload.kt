package org.picture2pc.picture2pc.data.repository.net.payload

import io.ktor.util.network.NetworkAddress
import io.ktor.util.network.hostname
import kotlinx.serialization.Serializable
import org.picture2pc.picture2pc.data.repository.net.peer.Peer

@Serializable
sealed class DiscoverPayload : Payload() {
    @Serializable
    data class ServiceOnline(val port: Int, val peerName: String, override val targetPeer: Peer) :
        DiscoverPayload() {
        val serviceAddresses: NetworkAddress?
            get() = this.receivedPayloadInfo?.senderInetSocketAddress?.hostname?.let {
                NetworkAddress(
                    it,
                    port
                )
            }
    }

    @Serializable
    data class ServicesList(override val targetPeer: Peer) : DiscoverPayload()
}
