package org.picture2pc.picture2pc.domain.repository.net.client

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.picture2pc.picture2pc.data.repository.net.payload.Payload

interface Client {
    val state: StateFlow<ClientState>
    val securityState: StateFlow<ClientSecurityState>

    val receivedPayloads: SharedFlow<Payload>
}