package com.github.picture2pc.common.net.di

import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast.MulticastConstants
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast.MulticastPayloadTransceiver
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast.SimpleMulticastSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.net.InetSocketAddress

val multicastModule = module {
    single(named("multicastCoroutineScope")) { CoroutineScope(Dispatchers.Default + SupervisorJob()) }

    single { MulticastPayloadTransceiver(get(named("multicastCoroutineScope")), get()) }
    factory {
        SimpleMulticastSocket(
            get(named("multicastCoroutineScope")),
            get(named("ioDispatcher")),
            get()
        )
    }
    single { InetSocketAddress(MulticastConstants.ADDRESS, MulticastConstants.PORT) }
}