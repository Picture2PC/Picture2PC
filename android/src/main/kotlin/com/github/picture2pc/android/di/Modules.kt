package com.github.picture2pc.android.di

import androidx.lifecycle.SavedStateHandle
import com.github.picture2pc.android.data.Device.Device
import com.github.picture2pc.android.data.ServerBroadcasting.ServerBroadcaster
import com.github.picture2pc.android.data.ServerBroadcasting.impl.MulticastServerBroadcaster
import com.github.picture2pc.android.viewmodel.MainScreenViewModels
import com.github.picture2pc.android.viewmodel.MainScreenViewModels.ClientsViewModel
import com.github.picture2pc.common.net.multicast.multicastModule
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext


val appModule = module {
    includes(multicastModule)

    single<CoroutineContext> { Dispatchers.IO }

    single{ClientsViewModel()}
    single { MainScreenViewModels.BroadcastViewModel(get(), get(), get()) }
    single<ServerBroadcaster>{MulticastServerBroadcaster(get(), Dispatchers.IO, get())}
    single{SavedStateHandle()}
    single { Device(get(), get()) }
}