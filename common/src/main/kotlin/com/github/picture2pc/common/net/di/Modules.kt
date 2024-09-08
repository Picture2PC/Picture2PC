package com.github.picture2pc.common.net.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val netAppModule = module {
    single(named("ioDispatcher")) { Dispatchers.IO }

    includes(multicastModule)
    includes(tcpConnectionModule)
}