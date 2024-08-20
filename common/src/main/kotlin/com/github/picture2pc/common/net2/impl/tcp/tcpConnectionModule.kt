package com.github.picture2pc.common.net2.impl.tcp

import org.koin.dsl.module

val tcpConnectionModule = module {
    factory { SimpleTcpServer(get()) }
    single { TcpPayloadTransceiver(get()) }
}