package com.github.picture2pc.desktop.di

import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.android.data.serverpreferences.impl.TestServerPreferencesRepository
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.android.net.datatransmitter.impl.MulticastTcpDataTransmitter
import com.github.picture2pc.common.di.commonAppModule
import com.github.picture2pc.desktop.data.clientpreferences.ClientPreferencesRepository
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.data.imageprep.impl.PicturePreparationImpl
import com.github.picture2pc.desktop.viewmodel.clientviewmodel.ClientViewModel
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

val appModule = module {
    includes(commonAppModule)

    single(named("backgroundCoroutineScope")) { CoroutineScope(Dispatchers.Default) }
    single(named("viewModelCoroutineScope")) { CoroutineScope(Dispatchers.Default) }

    single<DataTransmitter> {
        MulticastTcpDataTransmitter(
            get(), get(), get(),
            get(named("backgroundCoroutineScope"))
        )
    }

    single<PicturePreparation> { PicturePreparationImpl() }

    single { ServersSectionViewModel(get(named("viewModelCoroutineScope")), get()) }
    single { PictureDisplayViewModel(get(named("viewModelCoroutineScope")), get(), get()) }

    single<ServerPreferencesRepository> { TestServerPreferencesRepository() }
    single { ClientPreferencesRepository(File(".client_name.txt")) }
    single { ClientViewModel(get(), get(named("viewModelCoroutineScope"))) }
}
