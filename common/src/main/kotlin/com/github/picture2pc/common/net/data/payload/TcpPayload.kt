package com.github.picture2pc.common.net.data.payload

import com.github.picture2pc.common.net.data.peer.Peer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString

@Serializable
sealed class TcpPayload : Payload() {
    // ------------Internal--------------
    @Serializable
    data class Ping(override val targetPeer: Peer) : TcpPayload()

    @Serializable
    data class Pong(override val targetPeer: Peer) : TcpPayload()
    // ------------Internal--------------

    @Serializable
    data class RequestName(override val targetPeer: Peer) : TcpPayload()

    @Serializable
    data class NameUpdate(val name: String, override val targetPeer: Peer) : TcpPayload()

    @Serializable
    data class Picture @OptIn(ExperimentalSerializationApi::class) constructor(
        @ByteString val picture: ByteArray,
        val corners: List<Pair<Float, Float>>?,
        override val targetPeer: Peer = Peer.any()
    ) :
        TcpPayload() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Picture

            if (!picture.contentEquals(other.picture)) return false
            if (targetPeer != other.targetPeer) return false

            return true
        }

        override fun hashCode(): Int {
            var result = picture.contentHashCode()
            result = 31 * result + targetPeer.hashCode()
            return result
        }
    }
}