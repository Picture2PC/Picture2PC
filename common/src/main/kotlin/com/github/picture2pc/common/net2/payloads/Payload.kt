package com.github.picture2pc.common.net2.payloads

import com.github.picture2pc.common.net2.NetworkPayloadTransceiver
import com.github.picture2pc.common.net2.Peer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
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
        val stream = ByteArrayOutputStream()
        val format = Json {
            encodeDefaults = true
        }
        format.encodeToStream(serializer(), this, stream)
        return ByteArrayInputStream(stream.toByteArray())
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun fromInputStream(
            inputStream: InputStream,
            inetSocketAddress: InetSocketAddress
        ): Payload {
            val packet = Json.decodeFromStream(serializer(), inputStream)
            packet.receivedFromInetSocketAddress = inetSocketAddress
            return packet
        }
    }
}