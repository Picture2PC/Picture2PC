package com.github.picture2pc.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.picture2pc.android.di.appModule
import com.github.picture2pc.android.ui.main.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.newCoroutineContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module


class MainActivity : ComponentActivity() {

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val coroutineContextProviderModule = module {
            factory { Dispatchers.IO.newCoroutineContext(Dispatchers.IO) }
        }
        startKoin {
            // Log Koin into Android logger

            androidLogger()
            // Reference Android context
            androidContext(this@MainActivity)


            modules(appModule, coroutineContextProviderModule)
        }

        setContent {
            Screen()
        }
    }
}
