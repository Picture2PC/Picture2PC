package com.github.picture2pc.android.ui.main.bigpicturescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.github.picture2pc.android.data.takeimage.ImageManager
import com.github.picture2pc.android.ui.main.bigpicturescreen.elements.BigPicture
import com.github.picture2pc.android.ui.main.camerascreen.elements.CameraPreview
import com.github.picture2pc.android.ui.main.camerascreen.elements.DisplayImage
import com.github.picture2pc.android.ui.main.camerascreen.elements.PictureButtons
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import org.koin.compose.koinInject
import org.koin.compose.rememberKoinInject

@Composable
fun BigPictureScreen(
){
    Box(Modifier.fillMaxSize()){
        BigPicture()
    }
}
