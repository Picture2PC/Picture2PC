package com.github.picture2pc.common.net.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val netAppModule = module {
    single<CoroutineDispatcher>(named("ioDispatcher")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("defaultDispatcher")) { Dispatchers.Default }

    includes(multicastModule)
    includes(tcpConnectionModule)
}