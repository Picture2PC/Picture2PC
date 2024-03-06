package com.github.picture2pc.common.net.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.InputStream
import java.net.InetAddress

open class DefaultDataPayloadTransceiver {
    val _outgoingPayloads = MutableSharedFlow<InputStream>()
    val outgoingPayloads = _outgoingPayloads.asSharedFlow()

    suspend fun emit(dataPayload: NetworkDataPayload) {
        dataPayload.emit(this)
    }
}