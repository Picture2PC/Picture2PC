package com.github.picture2pc.desktop

import com.github.picture2pc.desktop.di.appModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.newCoroutineContext
import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.opencv_java
import org.koin.core.context.startKoin
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
fun main() {
    if (InstanceChecker.isAppAlreadyRunning()) return

    module {
        factory { Dispatchers.IO.newCoroutineContext(Dispatchers.IO) }
    }

    Loader.load(opencv_java::class.java)

    startKoin {
        allowOverride(false)
        modules(appModule)
    }

    runDesktopApp()
}