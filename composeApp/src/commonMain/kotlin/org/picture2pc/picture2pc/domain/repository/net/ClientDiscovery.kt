package org.picture2pc.picture2pc.domain.repository.net

import kotlinx.coroutines.flow.Flow
import org.picture2pc.picture2pc.data.repository.net.payload.DiscoverPayload

interface ClientDiscovery {
    val available: Boolean
    fun discover(serviceOnlineProvider: () -> (DiscoverPayload.ServiceOnline)): Flow<DiscoverPayload.ServiceOnline>
}