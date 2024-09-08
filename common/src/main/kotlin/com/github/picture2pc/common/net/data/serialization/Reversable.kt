package com.github.picture2pc.common.net.data.serialization

import com.github.picture2pc.common.net.data.packet.Packet
import com.github.picture2pc.common.net.data.payload.Payload
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
fun Payload.Companion.fromByteArray(byteArray: ByteArray): Payload {
    return Serializer.format.decodeFromByteArray(serializer(), byteArray)
}

@OptIn(ExperimentalSerializationApi::class)
fun Packet.Companion.fromByteArray(byteArray: ByteArray): Packet {
    return Serializer.format.decodeFromByteArray(serializer(), byteArray)
}