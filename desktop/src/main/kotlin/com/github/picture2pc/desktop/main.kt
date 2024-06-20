package com.github.picture2pc.desktop

import com.github.picture2pc.desktop.di.appModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.newCoroutineContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class, InternalCoroutinesApi::class)
fun main() {
    val coroutineContextProviderModule = module {
        factory { Dispatchers.IO.newCoroutineContext(Dispatchers.IO) }
    }

    startKoin {
        allowOverride(false)
        modules(appModule, coroutineContextProviderModule)
    }

    runDesktopApp()
}