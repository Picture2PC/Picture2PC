package org.picture2pc.picture2pc.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.picture2pc.picture2pc.data.repository.PreferencesMultiplatformSettings
import org.picture2pc.picture2pc.data.repository.net.impl.multicastPayloadTransceiver.MulticastPayloadTransceiver
import org.picture2pc.picture2pc.domain.repository.PreferencesRepository
import org.picture2pc.picture2pc.domain.repository.net.ClientDiscovery
import org.picture2pc.picture2pc.domain.usecase.PreferencesUseCase
import org.picture2pc.picture2pc.presentation.app.viewmodel.AppViewModel

expect val platformModule: Module

val qualifiers = module {
    single(DefaultDispatcherQualifier) { Dispatchers.Default }
    single(IODispatcherQualifier) { Dispatchers.IO }
    factory(BackgroundCoroutineScope) {
        CoroutineScope(
            get<CoroutineDispatcher>(
                DefaultDispatcherQualifier
            ) + SupervisorJob()
        )
    }
}

val preferencesModule = module {
    single {
        PreferencesMultiplatformSettings(
            get(),
            get(BackgroundCoroutineScope),
        )
    } bind PreferencesRepository::class
    single {
        PreferencesUseCase(get())
    }
}

val netModule = module {
    single {
        MulticastPayloadTransceiver(
            get(BackgroundCoroutineScope),
            get(IODispatcherQualifier)
        )
    } bind ClientDiscovery::class
}
val sharedModule = module {
    includes(qualifiers, preferencesModule, netModule)



    viewModelOf(::AppViewModel)
}