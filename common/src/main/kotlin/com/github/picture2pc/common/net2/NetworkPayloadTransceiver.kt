package com.github.picture2pc.common.net2

import com.github.picture2pc.common.net2.payloads.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.UUID

@OptIn(ExperimentalSerializationApi::class)
abstract class NetworkPayloadTransceiver : CoroutineScope {
    private val _receivedPayloads: MutableSharedFlow<Payload> = MutableSharedFlow()
    val receivedPayloads: SharedFlow<Payload> = _receivedPayloads.asSharedFlow()

    protected suspend fun receivedPayload(payload: Payload) {
        if (payload.targetPeer == getSelf() || payload.targetPeer.isAny) {
            _receivedPayloads.emit(payload)
        }
    }

    private var lock = MutableStateFlow(0)
    private var lockQueue = 0
    suspend fun sendPayload(payload: Payload): Boolean {

        return coroutineScope {
            val c = lockQueue++
            while (lock.value != c) {
                lock.singleOrNull()
            }
            //return false if failed
            val res = _sendPayload(payload)
            if (lock.value == lockQueue - 1) {
                lockQueue = 0
                lock.value = 0
            } else
                lock.value = c + 1

            return@coroutineScope res
        }

    }

    protected abstract suspend fun _sendPayload(payload: Payload): Boolean

    companion object {
        val uuid = UUID.randomUUID().toString()
        var name = "test"
        fun getSelf(): Peer {
            return Peer(name, uuid, false)
        }
    }
}