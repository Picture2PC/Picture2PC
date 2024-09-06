package com.github.picture2pc.common.di

import com.github.picture2pc.common.net.di.netAppModule
import org.koin.dsl.module

val commonAppModule = module {
    includes(netAppModule)
}