package com.github.picture2pc.common.net.multicast

import org.koin.dsl.module

val multicastModule = module {
    factory { MulticastPayloadTransciever(get(), get()) }
    factory { SimpleMulticastSocket(MulticastConstants.address, MulticastConstants.port) }
}