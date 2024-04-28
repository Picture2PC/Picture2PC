package com.github.picture2pc.common.net2

import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID

interface NetworkPayloadTransceiver {
    val receivedPayloads: SharedFlow<Payload>

    fun sendPayload(payload: Payload)

    companion object {
        val uuid = UUID.randomUUID().toString()
        val name = "test"
        fun getSelf(): Peer {
            return Peer(name, uuid, false)
        }
    }

}