package org.picture2pc.picture2pc.domain.repository.net.client

import kotlinx.coroutines.flow.StateFlow

interface Client {
    val name: StateFlow<String>
    val state: StateFlow<ClientState>
    val securityState: StateFlow<ClientSecurityState>

    //val receivedPayloads: SharedFlow<Payload>
}