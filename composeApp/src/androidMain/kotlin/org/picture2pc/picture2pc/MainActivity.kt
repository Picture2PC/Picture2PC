package org.picture2pc.picture2pc

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.environmentProperties
import org.picture2pc.picture2pc.di.initKoin
import org.picture2pc.picture2pc.ui.app.screens.GroupSelectGroupSelect

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@AndroidApp)
            environmentProperties()
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            GroupSelectGroupSelect()
        }
    }
}
