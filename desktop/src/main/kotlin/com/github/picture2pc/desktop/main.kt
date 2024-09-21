package com.github.picture2pc.desktop

import com.github.picture2pc.desktop.di.appModule
import com.github.picture2pc.desktop.util.InstanceChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.newCoroutineContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.opencv.core.Core

@OptIn(InternalCoroutinesApi::class)
fun main() {
    if (InstanceChecker.isAppAlreadyRunning()) return

    module {
        factory { Dispatchers.IO.newCoroutineContext(Dispatchers.IO) }
    }

    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    startKoin {
        allowOverride(false)
        modules(appModule)
    }

    runDesktopApp()
}