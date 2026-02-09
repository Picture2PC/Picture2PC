package org.picture2pc.picture2pc.data.repository.net.multicastPayloadTransceiver

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.picture2pc.picture2pc.domain.repository.net.ClientDiscovery
import org.picture2pc.picture2pc.domain.repository.net.payload.DiscoverPayload

expect class MulticastPayloadTransceiver(scope: CoroutineScope, ioDispatcher: CoroutineDispatcher, logger: Logger) :
    ClientDiscovery {
    override val available: Boolean

    override fun discover(serviceOnlineProvider: () -> DiscoverPayload.ServiceOnline): Flow<DiscoverPayload.ServiceOnline>
}