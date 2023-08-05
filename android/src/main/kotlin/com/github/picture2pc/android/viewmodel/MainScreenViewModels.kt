package com.github.picture2pc.android.viewmodel

import com.github.picture2pc.android.data.Device.Device
import com.github.picture2pc.android.data.ServerBroadcasting.ServerBroadcaster
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object MainScreenViewModels {

    class BroadcastViewModel(private val device: Device, private val broadcaster: ServerBroadcaster) {
        val name = device.nameState
        var serving :Boolean
            get() {
                return device.serving.value
            }
            set(value){
                if (value){
                    broadcaster.start()
                }
                else{
                    broadcaster.stop()
                }

            }

        fun CheckedChanged(value: Boolean){
            serving = value
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