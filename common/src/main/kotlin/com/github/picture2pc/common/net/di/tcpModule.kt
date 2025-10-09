package com.github.picture2pc.common.net.di

import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.SimpleTcpServer
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.TcpPayloadTransceiver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val tcpConnectionModule = module {
    single(named("tcpCoroutineScope")) { 
        CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob()) 
    }

    factory { SimpleTcpServer(get(named("tcpCoroutineScope")), get(named("ioDispatcher"))) }
    single { TcpPayloadTransceiver(get(named("tcpCoroutineScope")), get()) }
}