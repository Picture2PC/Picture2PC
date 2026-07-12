package org.picture2pc.picture2pc.di

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module

actual val platformModule = module {
    single<ObservableSettings> {
        val factory: SharedPreferencesSettings.Factory = SharedPreferencesSettings.Factory(get())
        return@single factory.create()
    }
}