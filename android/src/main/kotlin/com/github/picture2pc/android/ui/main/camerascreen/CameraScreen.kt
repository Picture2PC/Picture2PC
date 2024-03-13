package com.github.picture2pc.android.ui.main.camerascreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.camerascreen.elements.TakePictureButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview

@Composable
fun CameraScreen(function: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        CameraPreview(
            modifier = Modifier
                .height(600.dp)
                .padding(30.dp)
        )
    }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        TakePictureButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onClick = { function() }
        )
    }
}