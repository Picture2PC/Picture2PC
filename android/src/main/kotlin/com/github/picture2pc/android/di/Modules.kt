package com.github.picture2pc.android.di

import androidx.lifecycle.SavedStateHandle
import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.android.data.serverpreferences.impl.DataStoreServerPreferencesRepository
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.github.picture2pc.android.data.takeimage.impl.CameraPictureManager
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.android.net.datatransmitter.impl.MulticastTcpDataTransmitter
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.ClientsViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.di.commonAppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    includes(commonAppModule)

    single(named("backgroundCoroutineScope")) { CoroutineScope(Dispatchers.Default) }
    single<DataTransmitter> {
        MulticastTcpDataTransmitter(
            get(),
            get(),
            get(),
            get(named("backgroundCoroutineScope"))
        )
    }
    single<ServerPreferencesRepository> {
        DataStoreServerPreferencesRepository(
            get(),
            get(named("backgroundCoroutineScope"))
        )
    }


    single { BroadcastViewModel(get()) }
    single { ClientsViewModel(get()) }
    single<PictureManager> { CameraPictureManager(get()) }
    single { CameraViewModel(get(), get()) }
    single { ScreenSelectorViewModel() }

    single { SavedStateHandle() }

}