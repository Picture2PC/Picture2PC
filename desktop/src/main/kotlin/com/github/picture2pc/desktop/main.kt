package com.github.picture2pc.desktop

import com.github.picture2pc.desktop.di.appModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.opencv.core.Core

@OptIn(ExperimentalCoroutinesApi::class)
fun main(): Unit = runBlocking {
    val coroutineContextProviderModule = module {
        factory { newCoroutineContext(Dispatchers.IO) }
    }

    //Load OpenCV native library
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    startKoin {
        allowOverride(false)
        modules(appModule, coroutineContextProviderModule)
    }

    launchDesktopApp()
}