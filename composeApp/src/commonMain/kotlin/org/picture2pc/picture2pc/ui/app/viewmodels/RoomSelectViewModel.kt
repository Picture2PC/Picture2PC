package org.picture2pc.picture2pc.ui.app.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RoomSelectViewModel : ViewModel() {
    var expanded by mutableStateOf(false)
    var roomName by mutableStateOf("")
}