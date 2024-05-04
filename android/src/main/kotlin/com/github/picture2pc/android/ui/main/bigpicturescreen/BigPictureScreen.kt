package com.github.picture2pc.android.ui.main.bigpicturescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.bigpicturescreen.elements.BigPicture

@Composable
fun BigPictureScreen(){
    Box(Modifier.fillMaxSize()){
        BigPicture()
    }
}