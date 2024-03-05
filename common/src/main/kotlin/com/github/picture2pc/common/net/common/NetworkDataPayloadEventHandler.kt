package com.github.picture2pc.common.net.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.InputStream
import java.net.InetAddress

open class NetworkDataPayloadEventHandler<T> {

    private val _incomingPayloads = MutableSharedFlow<NetworkReceivedPayload<T>>()
    val incomingPayloads = _incomingPayloads.asSharedFlow()

    companion object{
        @JvmStatic
        val _outgoingPayloads = MutableSharedFlow<InputStream>()
        @JvmStatic
        val outgoingPayloads = _outgoingPayloads.asSharedFlow()
    }

    suspend fun newEvent(p: T, address: InetAddress){
        _incomingPayloads.emit(NetworkReceivedPayload(p, address));
    }
}
