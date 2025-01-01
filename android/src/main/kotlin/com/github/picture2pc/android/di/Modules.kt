package com.github.picture2pc.android.di

import androidx.lifecycle.SavedStateHandle
import com.github.picture2pc.android.data.edgedetection.EdgeDetect
import com.github.picture2pc.android.data.edgedetection.impl.YOLOv8SegEdgeDetect
import com.github.picture2pc.android.data.galleryimageselection.GalleryManager
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    includes(commonAppModule)

    single(named("defaultDispatcher")) { Dispatchers.Default }
    single(named("backgroundCoroutineScope")) { CoroutineScope(get<CoroutineDispatcher>(named("defaultDispatcher")) + SupervisorJob()) }
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

    single<GalleryManager> { GalleryManager(get()) }


    single { BroadcastViewModel(get()) }
    single<EdgeDetect> { YOLOv8SegEdgeDetect(get(named("ioDispatcher"))) }
    single { ClientsViewModel(get()) }
    single<PictureManager> {
        CameraPictureManager(
            get(),
            get(),
            get(named("backgroundCoroutineScope")),
            get(named("defaultDispatcher"))
        )
    }
    single { CameraViewModel(get(), get()) }
    single { ScreenSelectorViewModel() }

    single { SavedStateHandle() }

}