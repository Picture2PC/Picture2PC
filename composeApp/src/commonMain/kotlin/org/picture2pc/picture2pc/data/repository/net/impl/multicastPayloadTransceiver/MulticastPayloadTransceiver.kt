package org.picture2pc.picture2pc.data.repository.net.impl.multicastPayloadTransceiver

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.picture2pc.picture2pc.data.repository.net.impl.MulticastTcpClient
import org.picture2pc.picture2pc.data.repository.net.payload.DiscoverPayload
import org.picture2pc.picture2pc.domain.repository.net.ClientDiscovery

expect class MulticastPayloadTransceiver(scope: CoroutineScope, ioDispatcher: CoroutineDispatcher) :
    ClientDiscovery {
    override val available: Boolean

    override fun discover(serviceOnline: DiscoverPayload.ServiceOnline): Flow<MulticastTcpClient>
}