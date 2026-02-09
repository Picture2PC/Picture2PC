package org.picture2pc.picture2pc.data.repository.net

import io.ktor.network.sockets.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.picture2pc.picture2pc.domain.repository.net.client.Client
import org.picture2pc.picture2pc.domain.repository.net.client.ClientSecurityState
import org.picture2pc.picture2pc.domain.repository.net.client.ClientState

class MulticastTcpClient(
    initialName: String,
    initialState: ClientState,
    initialSecurityState: ClientSecurityState,
    val inetSocketAddress: InetSocketAddress
) :
    Client {
    val _name = MutableStateFlow(initialName)
    override val name = _name.asStateFlow()
    val _state = MutableStateFlow(initialState)
    override val state = _state.asStateFlow()

    val _securityState = MutableStateFlow(initialSecurityState)
    override val securityState = _securityState.asStateFlow()

    companion object {
        fun unknown(inetSocketAddress: InetSocketAddress): MulticastTcpClient {
            return MulticastTcpClient(
                "Unknown",
                ClientState.ONLINE,
                ClientSecurityState.PeerUnknown,
                inetSocketAddress
            )
        }
    }

//    private val _receivedPayloads = MutableSharedFlow<Payload>()
//    override val receivedPayloads = _receivedPayloads.asSharedFlow()
//
//    suspend fun receivedPayload(payload: Payload) {
//        if (payload.targetPeer == Peer.Companion.getSelf() || (payload.targetPeer.isAny && payload.sourcePeer != Peer.Companion.getSelf())) {
//            _receivedPayloads.emit(payload)
//        }
//    }
}

