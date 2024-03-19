package com.github.picture2pc.android.ui.main.camerascreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview
import com.github.picture2pc.android.ui.main.camerascreen.elements.DisplayImage
import com.github.picture2pc.android.ui.main.camerascreen.elements.TakePictureButton
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun CameraScreen(
    functionReturn: () -> Unit,
    functionSend: () -> Unit,
    viewModel: CameraViewModel = rememberKoinInject()
){
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        TakePictureButton(
            onClickReturn = { functionReturn() },
            onClickPicture = { viewModel.takeImage() },
            onClickSend = { functionSend() }
        )
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel
        )
    }
    DisplayImage(viewModel.getLastImage())
}