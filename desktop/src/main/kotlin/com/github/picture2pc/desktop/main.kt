package com.github.picture2pc.desktop

import com.github.picture2pc.desktop.di.appModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.newCoroutineContext
import nu.pattern.OpenCV
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.opencv.core.Core

@OptIn(InternalCoroutinesApi::class)
fun main() {
    module {
        factory { Dispatchers.IO.newCoroutineContext(Dispatchers.IO) }
    }

    OpenCV.loadLocally()
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    startKoin {
        allowOverride(false)
        modules(appModule)
    }

    runDesktopApp()
}