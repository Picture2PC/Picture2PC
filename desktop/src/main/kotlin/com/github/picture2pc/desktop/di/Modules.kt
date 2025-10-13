package com.github.picture2pc.desktop.di

import com.github.picture2pc.common.data.preferences.PreferencesRepository
import com.github.picture2pc.common.di.commonAppModule
import com.github.picture2pc.common.ui.notification.NotificationHandler
import com.github.picture2pc.desktop.data.TrayNotificationHandler
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.data.imageprep.impl.PicturePreparationImpl
import com.github.picture2pc.desktop.data.preferences.impl.DesktopPreferencesRepository
import com.github.picture2pc.desktop.net.datatransmitter.DataTransmitter
import com.github.picture2pc.desktop.net.datatransmitter.impl.MulticastTcpDataTransmitter
import com.github.picture2pc.desktop.viewmodel.mainscreen.BroadcastViewModel
import com.github.picture2pc.desktop.viewmodel.mainscreen.MovementHandlerViewModel
import com.github.picture2pc.desktop.viewmodel.mainscreen.PictureDisplayViewModel
import com.github.picture2pc.desktop.viewmodel.mainscreen.ServersSectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    includes(commonAppModule)

    single(named("backgroundCoroutineScope")) { CoroutineScope(Dispatchers.Default) }
    single(named("viewModelCoroutineScope")) { CoroutineScope(Dispatchers.Default) }
    single<NotificationHandler> { TrayNotificationHandler() }

    single<DataTransmitter> {
        MulticastTcpDataTransmitter(
            get(),
            get(),
            get(),
            get(named("backgroundCoroutineScope"))
        )
    }
    single {
        DesktopPreferencesRepository(
            get(named("backgroundCoroutineScope")),
            get(named("ioDispatcher"))
        )
    } bind PreferencesRepository::class
    single<PicturePreparation> { PicturePreparationImpl() }

    single { ServersSectionViewModel(get(), get(named("viewModelCoroutineScope"))) }
    single { MovementHandlerViewModel() }
    single {
        PictureDisplayViewModel(
            get(named("viewModelCoroutineScope")),
            get(),
            get(),
            get(),
            get(),
        )
    }
    single { BroadcastViewModel(get(named("viewModelCoroutineScope")), get()) }
}
