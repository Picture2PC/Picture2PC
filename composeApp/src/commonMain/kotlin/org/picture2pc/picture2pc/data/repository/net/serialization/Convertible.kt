package org.picture2pc.picture2pc.data.repository.net.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer
import org.picture2pc.picture2pc.data.repository.net.packet.Packet
import org.picture2pc.picture2pc.data.repository.net.payload.Payload


@OptIn(ExperimentalSerializationApi::class)
fun Payload.asByteArray(): ByteArray {
    return Serializer.Companion.format.encodeToByteArray(serializer(), this)
}

@OptIn(ExperimentalSerializationApi::class)
fun Packet.asByteArray(): ByteArray {
    return Serializer.Companion.format.encodeToByteArray(serializer(), this)
}