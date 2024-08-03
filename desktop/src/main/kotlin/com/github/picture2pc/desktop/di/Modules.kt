package com.github.picture2pc.desktop.di

import com.github.picture2pc.common.di.commonAppModule
import com.github.picture2pc.desktop.data.availableserverscollector.AvailableServersCollector
import com.github.picture2pc.desktop.data.availableserverscollector.impl.MulticastAvailableServersCollector
import com.github.picture2pc.desktop.net.datatransmitter.DataReceiver
import com.github.picture2pc.desktop.net.datatransmitter.impl.TcpPictureReceiver
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import com.github.picture2pc.desktop.viewmodel.pictureeditorviewmodel.PictureEditorViewModel
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.dsl.module

val appModule = module {
    includes(commonAppModule)

    single<AvailableServersCollector> { MulticastAvailableServersCollector(get(), get(), get()) }
    single<DataReceiver> { TcpPictureReceiver(get(), get()) }

    single { ServersSectionViewModel(get(), get()) }
    single { PictureDisplayViewModel(get(), get()) }
    single { PictureEditorViewModel(get()) }
}