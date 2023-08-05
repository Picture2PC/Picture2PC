package com.github.picture2pc.android.ui.main.MainScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.MainScreen.elements.Clients
import com.github.picture2pc.android.ui.main.MainScreen.elements.Connection
import com.github.picture2pc.android.ui.main.MainScreen.elements.Footer
import com.github.picture2pc.android.ui.main.MainScreen.elements.Header
import com.github.picture2pc.android.ui.main.MainScreen.elements.Progress


@Composable
fun MainScreen() {

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(10.dp))
        Header(
            Modifier
                .fillMaxWidth()
                .padding(10.dp))
        //NameInput(modifier = Modifier.fillMaxWidth())
        Connection(
            Modifier
                .padding(40.dp)
                .fillMaxWidth())
        Progress(
            Modifier
                .fillMaxWidth()
                .padding(50.dp, 0.dp, 50.dp, 10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Clients(modifier = Modifier
            .fillMaxSize()
            .padding(60.dp, 20.dp)
            .weight(1F, false))
        Footer(modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 20.dp))

    }
}
