package com.github.picture2pc.common.net.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.InetAddress
@Serializable
sealed interface NetworkDataPayload
{
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun emit(defaultDataPayloadTransceiver: DefaultDataPayloadTransceiver) {
        val s = ByteArrayOutputStream()
        Json.encodeToStream(this, stream = s)
        defaultDataPayloadTransceiver._outgoingPayloads.emit(ByteArrayInputStream(s.toByteArray()))
    }
    suspend fun newEvent(p: Any, address: InetAddress)
}


object NetworkDataPayloads {
    @Serializable
    class ListServers : NetworkDataPayload {
        companion object : NetworkDataPayloadEventHandler<ListServers>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ListServers, address)
        }

    }

    @Serializable
    data class ServerOnline(val deviceName: String) : NetworkDataPayload {
        companion object : NetworkDataPayloadEventHandler<ServerOnline>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as ServerOnline, address)
        }
    }

    @Serializable
    class Ping : NetworkDataPayload {
        companion object : NetworkDataPayloadEventHandler<Ping>()

        override suspend fun newEvent(p: Any, address: InetAddress) {
            Companion.newEvent(p as Ping, address)
        }

    }
}
