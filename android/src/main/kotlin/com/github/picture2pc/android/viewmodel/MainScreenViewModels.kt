package com.github.picture2pc.android.viewmodel

import com.github.picture2pc.android.data.Device.Device
import com.github.picture2pc.android.data.ServerBroadcasting.ServerBroadcaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object MainScreenViewModels {
    class BroadcastViewModel(private val device: Device, private val broadcaster: ServerBroadcaster, override val coroutineContext: CoroutineContext):CoroutineScope{
        val name = device.name.asStateFlow()
        var serving = device.serving.asStateFlow()

        fun checkedChanged(value: Boolean){
            launch{device.serving.emit(value)}
        }
        fun nameChanged(value: String)
        {
            launch { device.name.emit(value) }
        }
    }



    class ClientsViewModel {
        private val _serverEntries = MutableStateFlow(listOf<ClientData>())
        val serverEntries = _serverEntries.asStateFlow()

        data class ClientData(
            val clientName: String,
            val clientAddress: String,
        ) {

            override fun hashCode(): Int {
                return clientAddress.hashCode()
            }
        }

    }
}