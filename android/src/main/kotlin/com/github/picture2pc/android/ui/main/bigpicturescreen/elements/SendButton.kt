package com.github.picture2pc.android.ui.main.bigpicturescreen.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.camerascreen.elements.shape
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun SendButton(
    cameraViewModel: CameraViewModel = rememberKoinInject(),
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject(),
) {
    Button(
        modifier = Modifier.padding(20.dp),
        onClick = { cameraViewModel.sendImage(); screenSelectorViewModel.toCamera() },
        shape = shape
    ) {
        Text(text = "Send")
    }
}