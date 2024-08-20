package com.github.picture2pc.android.ui.main.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.mainscreen.elements.BrandingTopBar
import com.github.picture2pc.android.ui.main.mainscreen.elements.ConnectableStateSwitch
import com.github.picture2pc.android.ui.main.mainscreen.elements.ConnectedClientsList
import com.github.picture2pc.android.ui.main.mainscreen.elements.ServerNameInputField
import com.github.picture2pc.android.ui.main.mainscreen.elements.StateInfoPictureButton
import com.github.picture2pc.android.ui.main.mainscreen.elements.TransmissionProgressbar
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun MainScreenHorizontal(screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.fillMaxWidth(0.5F)) {
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
                        .padding(50.dp, 0.dp, 50.dp, 30.dp)
                )
                StateInfoPictureButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp, 0.dp, 10.dp),
                    onClick = screenSelectorViewModel::toCamera
                )
            }
            Column(modifier = Modifier.fillMaxSize()) {
                BrandingTopBar(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                ConnectedClientsList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(60.dp, 20.dp)
                        .weight(1F, false)
                )

            }
        }
    }
}
