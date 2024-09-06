package com.github.picture2pc.common.net.data.client

import com.github.picture2pc.common.net.data.packet.Packet
import com.github.picture2pc.common.net.data.payload.Payload
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.net.data.peer.Peer
import com.github.picture2pc.common.net.data.serialization.asByteArray
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class Client {
    abstract val receivedPayloads: SharedFlow<Payload>
    abstract val peer: Peer

    protected val _clientStateFlow = MutableStateFlow<ClientState>(ClientState.ONLINE)
    val clientStateFlow = _clientStateFlow.asStateFlow()


    protected fun getByteArray(payload: Payload): ByteArray {
        val data = payload.asByteArray()
        val packet = Packet(TcpPayload::class.qualifiedName!!, data.size, payload.sourcePeer)
        return packet.asByteArray().plus(Byte.MIN_VALUE).plus(data)
    }
}