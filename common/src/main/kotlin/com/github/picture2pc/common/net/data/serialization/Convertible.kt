package com.github.picture2pc.common.net.data.serialization

import com.github.picture2pc.common.net.data.packet.Packet
import com.github.picture2pc.common.net.data.payload.Payload
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer


@OptIn(ExperimentalSerializationApi::class)
fun Payload.asByteArray(): ByteArray {
    return Serializer.format.encodeToByteArray(serializer(), this)
}

@OptIn(ExperimentalSerializationApi::class)
fun Packet.asByteArray(): ByteArray {
    return Serializer.format.encodeToByteArray(serializer(), this)
}