package com.github.picture2pc.android

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.picture2pc.android.di.appModule
import com.github.picture2pc.android.ui.main.Screen
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.environmentProperties

class MainActivity : ComponentActivity() {

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(this, CAMERAX_PERMISSONS, 0)
        }
        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@MainActivity)
            environmentProperties()

            modules(appModule)
        }

        setContent {
            Screen(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContent {
            Screen(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        }
    }

    private fun hasRequiredPermissions() = CAMERAX_PERMISSONS.all {
        return CAMERAX_PERMISSONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSONS = arrayOf(
            android.Manifest.permission.CAMERA,
        )
    }
}
