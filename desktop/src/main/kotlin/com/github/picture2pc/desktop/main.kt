package com.github.picture2pc.desktop

import com.github.picture2pc.desktop.di.appModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.context.startKoin
import org.koin.environmentProperties
import org.opencv.core.Core

@OptIn(ExperimentalCoroutinesApi::class, InternalCoroutinesApi::class)
fun main() {
    //Load OpenCV native library
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    startKoin {
        allowOverride(false)
        environmentProperties()
        modules(appModule)
    }

    runDesktopApp()
}