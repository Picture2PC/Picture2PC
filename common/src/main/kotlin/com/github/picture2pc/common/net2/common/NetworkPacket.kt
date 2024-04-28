package com.github.picture2pc.common.net2.common

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import kotlin.math.min

data class NetworkPacket(
    private val inputStream: InputStream? = null,
    var address: SocketAddress? = null,
    val packetSize: Int = NetworkConstants.PacketSize
) {
    var totalSize: Int? = inputStream?.available()
    var lastByteArray: ByteArray? = null
    private var datagramPacket: DatagramPacket? = null
    val available: Boolean
        get() = totalSize == null || sendAmount != totalSize

    var sendAmount: Int = 0

    fun getInputStream(): InputStream {
        return (inputStream ?: ByteArrayInputStream(lastByteArray))
    }

    fun getAddress(): InetAddress {
        return datagramPacket!!.address
    }

    fun getDatagramPacket(): DatagramPacket? {
        if (inputStream == null) {
            if (lastByteArray == null) {
                lastByteArray = ByteArray(Int.SIZE_BYTES)
                datagramPacket = DatagramPacket(lastByteArray, Int.SIZE_BYTES)
                return datagramPacket
            } else if (totalSize == null) {
                totalSize = ByteBuffer.wrap(lastByteArray).int
                lastByteArray = ByteArray(totalSize!!)
            }
            val a = min(packetSize, totalSize!! - sendAmount)
            sendAmount += a
            return DatagramPacket(lastByteArray, sendAmount - a, a)

        }
        if (lastByteArray == null) {
            lastByteArray = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(totalSize!!).array()
            return DatagramPacket(lastByteArray, Int.SIZE_BYTES, address)
        }
        if (!available) return null
        val a = min(packetSize, totalSize!! - sendAmount)
        sendAmount += a
        lastByteArray = ByteArray(a)
        inputStream.read(lastByteArray!!)
        return DatagramPacket(lastByteArray, a, address)
    }


}