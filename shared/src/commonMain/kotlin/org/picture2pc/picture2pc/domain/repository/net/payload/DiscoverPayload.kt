package org.picture2pc.picture2pc.domain.repository.net.payload

import io.ktor.util.network.*
import kotlinx.serialization.Serializable
import org.picture2pc.picture2pc.data.repository.net.common.peer.Peer
import org.picture2pc.picture2pc.domain.repository.group.Room

@Serializable
sealed class DiscoverPayload : Payload() {
    @Serializable
    data class ServiceOnline(val port: Int, val peerName: String, val room: Room?, override val targetPeer: Peer) :
        DiscoverPayload() {
        val serviceAddress: NetworkAddress?
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
