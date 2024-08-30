package com.github.picture2pc.common.net.payloads

import com.github.picture2pc.common.net.NetworkPayloadTransceiver
import com.github.picture2pc.common.net.Peer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.cbor.Cbor
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.InetSocketAddress

@Serializable
sealed class Payload {
    abstract val targetPeer: Peer
    val sourcePeer: Peer = NetworkPayloadTransceiver.getSelf()


    // will be set by the transceiver
    @Transient
    var receivedFromInetSocketAddress: InetSocketAddress? = null

    @OptIn(ExperimentalSerializationApi::class)
    fun asInputStream(): ByteArrayInputStream {
        return ByteArrayInputStream(format.encodeToByteArray(serializer(), this))
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun fromInputStream(
            inputStream: InputStream,
            inetSocketAddress: InetSocketAddress
        ): Payload {
            val packet: Payload = Cbor.decodeFromByteArray(serializer(), inputStream.readBytes())
            packet.receivedFromInetSocketAddress = inetSocketAddress
            return packet
        }

        @OptIn(ExperimentalSerializationApi::class)
        private val format = Cbor {
            encodeDefaults = true
        }
    }
}