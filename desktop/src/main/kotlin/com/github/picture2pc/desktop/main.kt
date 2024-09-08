package com.github.picture2pc.desktop

import com.github.picture2pc.desktop.di.appModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.newCoroutineContext
import nu.pattern.OpenCV
import org.koin.core.context.startKoin
import org.koin.dsl.module


@OptIn(InternalCoroutinesApi::class)
fun main() {
    val coroutineContextProviderModule = module {
        factory { Dispatchers.IO.newCoroutineContext(Dispatchers.IO) }
    }

    OpenCV.loadLocally()
    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)

    startKoin {
        allowOverride(false)
        modules(appModule)
    }

    runDesktopApp()
}