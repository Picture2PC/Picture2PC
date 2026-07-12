package org.picture2pc.picture2pc.di

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import org.koin.dsl.module

actual val platformModule = module {
    single<ObservableSettings> {
        val factory: PreferencesSettings.Factory = PreferencesSettings.Factory()
        return@single factory.create()
    }
}