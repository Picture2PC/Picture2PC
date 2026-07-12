package org.picture2pc.picture2pc.domain.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.serializer
import org.picture2pc.picture2pc.data.repository.net.common.packet.Packet
import org.picture2pc.picture2pc.domain.repository.group.InternalGroup
import org.picture2pc.picture2pc.domain.repository.net.payload.Payload


@OptIn(ExperimentalSerializationApi::class)
fun Payload.asByteArray(): ByteArray {
    return Serializer.format.encodeToByteArray(serializer(), this)
}

@OptIn(ExperimentalSerializationApi::class)
fun Packet.asByteArray(): ByteArray {
    return Serializer.format.encodeToByteArray(serializer(), this)
}

@OptIn(ExperimentalSerializationApi::class)
fun List<InternalGroup>.asString(): String {
    return Serializer.format.encodeToHexString(serializer(), this)
}