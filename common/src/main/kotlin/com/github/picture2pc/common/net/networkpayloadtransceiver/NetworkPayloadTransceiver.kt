package com.github.picture2pc.common.net.networkpayloadtransceiver

import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.peer.Peer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
abstract class NetworkPayloadTransceiver : CoroutineScope {
    private val _receivedPayloads: MutableSharedFlow<Payload> = MutableSharedFlow()
    val receivedPayloads: SharedFlow<Payload> = _receivedPayloads.asSharedFlow()

    protected suspend fun receivedPayload(payload: Payload) {
        if (payload.targetPeer == Peer.getSelf() || (payload.targetPeer.isAny && payload.sourcePeer != Peer.getSelf())) {
            _receivedPayloads.emit(payload)
        }
    }

    suspend fun sendPayload(payload: Payload): Boolean {
        return coroutineScope { _sendPayload(payload) }
    }

    protected abstract suspend fun _sendPayload(payload: Payload): Boolean

    companion object {
        init {
            System.setProperty("java.net.preferIPv4Stack", "true")
        }
    }
}