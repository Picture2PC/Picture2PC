package com.github.picture2pc.common.net.data.serialization

import com.github.picture2pc.common.net.data.packet.Packet
import com.github.picture2pc.common.net.data.payload.Payload
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer
import org.koin.ext.getFullName


@OptIn(ExperimentalSerializationApi::class)
fun Payload.asByteArray(): ByteArray {
    return Serializer.format.encodeToByteArray(serializer(), this)
}

@OptIn(ExperimentalSerializationApi::class)
fun Packet.asByteArray(): ByteArray {
    return Serializer.format.encodeToByteArray(serializer(), this)
}

/**
 * Encodes a Payload into a byte array using a length-prefix protocol.
 * 
 * Protocol format:
 * [4-byte header length][header bytes][payload bytes]
 * 
 * The 4-byte length prefix is encoded as a big-endian integer representing
 * the size of the packet header in bytes. This approach ensures that any
 * byte value (including Byte.MIN_VALUE) can appear in the header without
 * being misinterpreted as a delimiter.
 */
fun Payload.getByteArray(): ByteArray {
    val data = this.asByteArray()
    val packet = Packet(this::class.getFullName(), data.size, this.sourcePeer)
    val packetBytes = packet.asByteArray()
    
    // Use 4-byte length prefix instead of delimiter
    val headerLength = packetBytes.size
    val lengthPrefix = ByteArray(4)
    lengthPrefix[0] = (headerLength shr 24).toByte()
    lengthPrefix[1] = (headerLength shr 16).toByte()
    lengthPrefix[2] = (headerLength shr 8).toByte()
    lengthPrefix[3] = headerLength.toByte()
    
    return lengthPrefix.plus(packetBytes).plus(data)
}
