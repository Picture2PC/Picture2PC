package com.github.picture2pc.android.di

import androidx.compose.runtime.mutableStateOf
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


    factory<CoroutineContext> { Dispatchers.Default }

    single{ClientsViewModel()}
    single { MainScreenViewModels.BroadcastViewModel(get(), get()) }
    single { Device(mutableStateOf(value = false), mutableStateOf("None")) }
    single<ServerBroadcaster>{MulticastServerBroadcaster(get(), get(), get())}

}