package com.github.picture2pc.common.net2.impl.multicast

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import java.net.InetSocketAddress

val multicastModule = module {
    single { MulticastPayloadTransceiver(get(), Dispatchers.IO) }
    single { InetSocketAddress(MulticastConstants.address, MulticastConstants.port) }
}