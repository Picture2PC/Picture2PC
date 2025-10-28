package org.picture2pc.picture2pc.data.repository.net.impl

import io.ktor.util.network.NetworkAddress
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.picture2pc.picture2pc.data.repository.net.payload.Payload
import org.picture2pc.picture2pc.data.repository.net.peer.Peer
import org.picture2pc.picture2pc.domain.repository.net.client.Client
import org.picture2pc.picture2pc.domain.repository.net.client.ClientSecurityState
import org.picture2pc.picture2pc.domain.repository.net.client.ClientState

class MulticastTcpClient(
    initialState: ClientState,
    initialSecurityState: ClientSecurityState,
    val networkAddress: NetworkAddress
) :
    Client {
    private val _state = MutableStateFlow(initialState)
    override val state = _state.asStateFlow()

    private val _securityState = MutableStateFlow(initialSecurityState)
    override val securityState = _securityState.asStateFlow()

    private val _receivedPayloads = MutableSharedFlow<Payload>()
    override val receivedPayloads = _receivedPayloads.asSharedFlow()

    suspend fun receivedPayload(payload: Payload) {
        if (payload.targetPeer == Peer.Companion.getSelf() || (payload.targetPeer.isAny && payload.sourcePeer != Peer.Companion.getSelf())) {
            _receivedPayloads.emit(payload)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is MulticastTcpClient) {
            if (other.networkAddress == this.networkAddress)
                return true
            if (other.securityState.value is ClientSecurityState.PeerKnown && this.securityState.value is ClientSecurityState.PeerKnown)
                return (other.securityState.value as ClientSecurityState.PeerKnown).peer == (this.securityState.value as ClientSecurityState.PeerKnown).peer
        }
        return super.equals(other)
    }
}