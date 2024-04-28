package com.github.picture2pc.common.net2.payloads

import com.github.picture2pc.common.net2.NetworkPayloadTransceiver
import com.github.picture2pc.common.net2.Peer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Serializable
abstract sealed class Payload {
    abstract val targetPeer: Peer
    val sourcePeer: Peer = NetworkPayloadTransceiver.getSelf()

    fun asInputStream(): ByteArrayInputStream {
        val stream = ByteArrayOutputStream()
        Json.encodeToStream(serializer(), this, stream)
        return ByteArrayInputStream(stream.toByteArray())
    }
}