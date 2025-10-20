package org.picture2pc.picture2pc.di

import org.koin.core.qualifier.named

val DefaultDispatcherQualifier = named("defaultDispatcher")
val BackgroundCoroutineScope = named("backgroundCoroutineScope")
