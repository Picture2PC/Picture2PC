package com.github.picture2pc.common.net.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

abstract class DefaultDataPayloadTransceiver {
    private val _outgoingPayloads = MutableSharedFlow<InputStream>()
    val outgoingPayloads = _outgoingPayloads.asSharedFlow()

    suspend fun emit(dataPayload: NetworkDataPayload) {
        val stream = ByteArrayOutputStream()
        Json.encodeToStream(NetworkDataPayload.serializer(), dataPayload, stream)
        _outgoingPayloads.emit(ByteArrayInputStream(stream.toByteArray()))
    }

}