package com.github.picture2pc.android.di

import androidx.lifecycle.SavedStateHandle
import com.github.picture2pc.android.data.TrayNotificationHandler
import com.github.picture2pc.android.data.edgedetection.EdgeDetect
import com.github.picture2pc.android.data.edgedetection.impl.YOLOv8SegEdgeDetect
import com.github.picture2pc.android.data.serverpreferences.impl.AndroidPreferencesRepository
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.github.picture2pc.android.data.takeimage.impl.CameraPictureManager
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.android.net.datatransmitter.impl.MulticastTcpDataTransmitter
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.ClientsViewModel
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.PicturePickerViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.data.preferences.PreferencesRepository
import com.github.picture2pc.common.di.commonAppModule
import com.github.picture2pc.common.ui.notification.NotificationHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.bind
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
    single {
        AndroidPreferencesRepository(
            get(),
            get(named("ioDispatcher")),
            get(named("backgroundCoroutineScope"))
        )
    } bind PreferencesRepository::class

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
    single { CameraViewModel(get(), get(), get()) }
    single { ScreenSelectorViewModel() }
    single {
        PicturePickerViewModel(
            get(named("backgroundCoroutineScope")),
            get(),
            get(named("ioDispatcher")),
            get(),
            get()
        )
    }
    single<NotificationHandler> { TrayNotificationHandler(get()) }

    single { SavedStateHandle() }

}