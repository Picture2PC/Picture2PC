package com.github.picture2pc.common.net.tcpconnection

import org.koin.dsl.module

val tcpConnectionModule = module {
    factory { SimpleTcpClient(get()) }
    single { SimpleTcpServer(get()) }
}