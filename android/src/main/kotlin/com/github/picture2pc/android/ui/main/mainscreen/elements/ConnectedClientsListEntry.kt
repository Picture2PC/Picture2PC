package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.github.picture2pc.common.net2.Peer

@Composable
fun Client(clientData: Peer) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = clientData.name, fontSize = 20.sp)
        Text(text = "clientData.address")
    }
}