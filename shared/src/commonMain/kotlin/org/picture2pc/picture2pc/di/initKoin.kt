package org.picture2pc.picture2pc.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.koin.KermitKoinLogger
import co.touchlab.kermit.koin.kermitLoggerModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(koinAppDeclaration: KoinAppDeclaration? = null) {
    val baseLogger = Logger(StaticConfig())
    val koinBaseLogger = baseLogger.withTag("Koin")
    startKoin {
        logger(KermitKoinLogger(koinBaseLogger))
        koinAppDeclaration?.invoke(this)
        modules(platformModule, sharedModule, kermitLoggerModule(koinBaseLogger))
    }
}