package com.github.picture2pc.android.di

import androidx.lifecycle.SavedStateHandle
import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.android.data.serverpreferences.impl.DataStoreServerPreferencesRepository
import com.github.picture2pc.android.data.takeimage.ImageManager
import com.github.picture2pc.android.data.takeimage.impl.CameraImageManager
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

    single { ClientsViewModel() }
    single { BroadcastViewModel(get(), get()) }
    single<ImageManager> { CameraImageManager(get()) }
    single { CameraViewModel(get()) }
    single<ServerOnlineNotifier>(createdAtStart = true) {
        MulticastServerOnlineNotifier(
            get(),
            get(),
            get()
        )
    }
    single { ScreenSelectorViewModel() }
    single { SavedStateHandle() }
    single<ServerPreferencesRepository> { DataStoreServerPreferencesRepository(get(), get()) }
}