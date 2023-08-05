package com.github.picture2pc.common.net.multicast

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val multicastModule = module {
    factory { MulticastPayloadTransceiver(get(), Dispatchers.IO ) }
    factory { SimpleMulticastSocket(MulticastConstants.address, MulticastConstants.port) }
}