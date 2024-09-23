package com.github.picture2pc.desktop.di

import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.android.data.serverpreferences.impl.TestServerPreferencesRepository
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.android.net.datatransmitter.impl.MulticastTcpDataTransmitter
import com.github.picture2pc.common.di.commonAppModule
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.data.imageprep.impl.PicturePreparationImpl
import com.github.picture2pc.desktop.viewmodel.mainscreen.MovementHandlerViewModel
import com.github.picture2pc.desktop.viewmodel.mainscreen.PictureDisplayViewModel
import com.github.picture2pc.desktop.viewmodel.mainscreen.ServersSectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    includes(commonAppModule)

    single(named("backgroundCoroutineScope")) { CoroutineScope(Dispatchers.Default) }
    single(named("viewModelCoroutineScope")) { CoroutineScope(Dispatchers.Default) }

    single<DataTransmitter> {
        MulticastTcpDataTransmitter(
            get(),
            get(),
            get(),
            get(named("backgroundCoroutineScope"))
        )
    }
    single<ServerPreferencesRepository> { TestServerPreferencesRepository() }

    single<PicturePreparation> { PicturePreparationImpl() }

    single { ServersSectionViewModel(get()) }
    single { MovementHandlerViewModel() }
    single {
        PictureDisplayViewModel(
            get(named("viewModelCoroutineScope")),
            get(),
            get(),
            get()
        )
    }
}
