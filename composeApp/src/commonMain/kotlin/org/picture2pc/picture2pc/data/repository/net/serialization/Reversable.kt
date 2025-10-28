package org.picture2pc.picture2pc.data.repository.net.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import org.picture2pc.picture2pc.data.repository.net.packet.Packet
import org.picture2pc.picture2pc.data.repository.net.payload.Payload

@OptIn(ExperimentalSerializationApi::class)
fun Payload.Companion.fromByteArray(byteArray: ByteArray): Payload {
    return Serializer.Companion.format.decodeFromByteArray(serializer(), byteArray)
}

@OptIn(ExperimentalSerializationApi::class)
fun Packet.Companion.fromByteArray(byteArray: ByteArray): Packet {
    return Serializer.Companion.format.decodeFromByteArray(serializer(), byteArray)
}