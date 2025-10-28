package org.picture2pc.picture2pc.domain.repository.net

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.picture2pc.picture2pc.data.repository.net.payload.Payload
import org.picture2pc.picture2pc.data.repository.net.peer.Peer

abstract class NetworkPayloadTransceiver {
    abstract val available: Boolean
    private val _receivedPayloads: MutableSharedFlow<Payload> = MutableSharedFlow()
    val receivedPayloads: SharedFlow<Payload> = _receivedPayloads.asSharedFlow()

    protected suspend fun receivedPayload(payload: Payload) {
        if (payload.targetPeer == Peer.Companion.getSelf() || (payload.targetPeer.isAny && payload.sourcePeer != Peer.Companion.getSelf())) {
            _receivedPayloads.emit(payload)
        }
    }

    abstract suspend fun start()

    suspend fun sendPayload(payload: Payload): Boolean {
        return _sendPayload(payload)
    }

    protected abstract suspend fun _sendPayload(payload: Payload): Boolean


}