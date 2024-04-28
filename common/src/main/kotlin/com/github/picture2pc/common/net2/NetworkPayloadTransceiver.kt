package com.github.picture2pc.common.net2

import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID

interface NetworkPayloadTransceiver {
    val receivedPayloads: SharedFlow<Payload>

    suspend fun sendPayload(payload: Payload)

    companion object {
        val uuid = UUID.randomUUID().toString()
        var name = "test"
        fun getSelf(): Peer {
            return Peer(name, uuid, false)
        }
    }

}