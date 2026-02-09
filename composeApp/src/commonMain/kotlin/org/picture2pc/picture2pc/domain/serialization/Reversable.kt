package org.picture2pc.picture2pc.domain.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.serializer
import org.picture2pc.picture2pc.data.repository.net.common.packet.Packet
import org.picture2pc.picture2pc.domain.repository.group.InternalGroup
import org.picture2pc.picture2pc.domain.repository.net.payload.Payload

@OptIn(ExperimentalSerializationApi::class)
fun Payload.Companion.fromByteArray(byteArray: ByteArray): Payload {
    return Serializer.format.decodeFromByteArray(serializer(), byteArray)
}

@OptIn(ExperimentalSerializationApi::class)
fun Packet.Companion.fromByteArray(byteArray: ByteArray): Packet {
    return Serializer.format.decodeFromByteArray(serializer(), byteArray)
}

@OptIn(ExperimentalSerializationApi::class)
fun String.asGroupList(): List<InternalGroup> {
    return Serializer.format.decodeFromHexString(serializer(), this)
}