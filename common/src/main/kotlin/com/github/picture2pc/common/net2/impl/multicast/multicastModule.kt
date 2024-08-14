package com.github.picture2pc.common.net2.impl.multicast

import org.koin.dsl.module
import java.net.InetSocketAddress

val multicastModule = module {
    single { MulticastPayloadTransceiver(get()) }
    factory { SimpleMulticastSocket(get(), get()) }
    single { InetSocketAddress(MulticastConstants.ADDRESS, MulticastConstants.PORT) }
}