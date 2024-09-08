package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.github.picture2pc.android.net.datatransmitter.DefaultDevice

@Composable
fun Client(clientData: DefaultDevice) {
    val clientName = clientData.name.collectAsState()

    Row(
        Modifier
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = clientName.value, fontSize = 20.sp)
        Text(text = "clientData.address")

    }
}