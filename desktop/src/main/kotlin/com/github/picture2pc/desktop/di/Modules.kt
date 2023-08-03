package com.github.picture2pc.desktop.di

import com.github.picture2pc.common.net.multicast.multicastModule
import com.github.picture2pc.desktop.data.AvailableServersCollector
import com.github.picture2pc.desktop.data.impl.MulticastAvailableServersCollector
import com.github.picture2pc.desktop.viewmodel.ServersSectionViewModel
import org.koin.dsl.module

val appModule = module {
    includes(multicastModule)

    single<AvailableServersCollector> { MulticastAvailableServersCollector(get(), get()) }
    single { ServersSectionViewModel(get(), get()) }
}