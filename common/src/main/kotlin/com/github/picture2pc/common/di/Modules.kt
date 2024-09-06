package com.github.picture2pc.common.di

import com.github.picture2pc.common.net.impl.multicast.multicastModule
import com.github.picture2pc.common.net.impl.tcp.tcpConnectionModule
import org.koin.dsl.module

val commonAppModule = module {
    includes(multicastModule)
    includes(tcpConnectionModule)
}