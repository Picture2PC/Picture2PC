package com.github.picture2pc.common.net.di

import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.multicast.multicastModule
import com.github.picture2pc.common.net.networkpayloadtransceiver.impl.tcp.tcpConnectionModule
import org.koin.dsl.module

val netAppModule = module {
    includes(multicastModule)
    includes(tcpConnectionModule)
}