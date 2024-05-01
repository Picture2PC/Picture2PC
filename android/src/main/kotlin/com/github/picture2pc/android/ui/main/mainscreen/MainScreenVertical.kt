package com.github.picture2pc.android.ui.main.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.mainscreen.elements.BrandingTopBar
import com.github.picture2pc.android.ui.main.mainscreen.elements.ConnectableStateSwitch
import com.github.picture2pc.android.ui.main.mainscreen.elements.ConnectedClientsList
import com.github.picture2pc.android.ui.main.mainscreen.elements.ServerNameInputField
import com.github.picture2pc.android.ui.main.mainscreen.elements.StateInfoPictureButton
import com.github.picture2pc.android.ui.main.mainscreen.elements.TransmissionProgressbar


@Composable
fun MainScreenVertical() {

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(10.dp))
        BrandingTopBar(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
        ServerNameInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp, 0.dp)
        )
        ConnectableStateSwitch(
            Modifier
                .padding(40.dp)
                .fillMaxWidth()
        )
        TransmissionProgressbar(
            Modifier
                .fillMaxWidth()
                .padding(50.dp, 0.dp, 50.dp, 10.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        ConnectedClientsList(
            modifier = Modifier
                .fillMaxSize()
                .padding(60.dp, 20.dp)
                .weight(1F, false)
        )
        StateInfoPictureButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 20.dp)
        )

    }
}
