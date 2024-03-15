package com.github.picture2pc.common.net.multicast

import org.koin.dsl.module
import java.net.NetworkInterface

val multicastModule = module {
    single { MulticastPayloadTransceiver(get(), get()) }
    single<NetworkInterface> {
        NetworkInterface.getNetworkInterfaces().asSequence().filter { i ->
            i.isUp && !i.isVirtual && !i.isLoopback && !i.name.startsWith(
                "vEthernet",
                true
            ) && i.supportsMulticast()
        }.maxByOrNull { r -> r.interfaceAddresses.size }!!
    }
    single { SimpleMulticastSocket(MulticastConstants.address, MulticastConstants.port, get()) }
}