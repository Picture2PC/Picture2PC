package com.github.picture2pc.android.di

import androidx.lifecycle.SavedStateHandle
import com.github.picture2pc.android.data.serverpreferences.ServerPreferencesRepository
import com.github.picture2pc.android.data.serverpreferences.impl.DataStoreServerPreferencesRepository
import com.github.picture2pc.android.net.serveronlinenotifier.ServerOnlineNotifier
import com.github.picture2pc.android.net.serveronlinenotifier.impl.MulticastServerOnlineNotifier
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.ClientsViewModel
import com.github.picture2pc.common.net.multicast.multicastModule
import org.koin.dsl.module


val appModule = module {
    includes(multicastModule)

    single { ClientsViewModel() }
    single { BroadcastViewModel(get(), get()) }
    single<ServerOnlineNotifier>(createdAtStart = true) {
        MulticastServerOnlineNotifier(
            get(),
            get(),
            get()
        )
    }
    single { SavedStateHandle() }
    single<ServerPreferencesRepository> { DataStoreServerPreferencesRepository(get(), get()) }
}