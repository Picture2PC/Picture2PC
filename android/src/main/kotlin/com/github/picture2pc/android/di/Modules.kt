package com.github.picture2pc.android.di

import androidx.lifecycle.SavedStateHandle
import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.android.data.serverpreferences.impl.DataStoreServerPreferencesRepository
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.github.picture2pc.android.data.takeimage.impl.CameraPictureManager
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.android.net.datatransmitter.impl.TcpPictureTransmitter
import com.github.picture2pc.android.net.serveronlinenotifier.ServerOnlineNotifier
import com.github.picture2pc.android.net.serveronlinenotifier.impl.MulticastServerOnlineNotifier
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.ClientsViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.di.commonAppModule
import org.koin.dsl.module

val appModule = module {
    includes(commonAppModule)

    single { ClientsViewModel(get(), get()) }
    single { BroadcastViewModel(get(), get()) }
    single<PictureManager> { CameraPictureManager(get()) }
    single { CameraViewModel(get(), get()) }
    single { ScreenSelectorViewModel() }
    single<DataTransmitter> {TcpPictureTransmitter(get(), get())}
    single<ServerOnlineNotifier>(createdAtStart = true) {
        MulticastServerOnlineNotifier(
            get(),
            get(),
            get(),
            get(),
        )
    }
    single { SavedStateHandle() }
    single<ServerPreferencesRepository> { DataStoreServerPreferencesRepository(get(), get()) }
}