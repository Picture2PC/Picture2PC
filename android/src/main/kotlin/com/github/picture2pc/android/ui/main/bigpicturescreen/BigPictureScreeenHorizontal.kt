package com.github.picture2pc.android.ui.main.bigpicturescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.bigpicturescreen.elements.BigPicture
import com.github.picture2pc.android.ui.main.bigpicturescreen.elements.SendButton
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun BigPictureScreenHorizontal(
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
) {
    Box(
        Modifier
            .fillMaxSize()
            .clickable(onClick = screenSelectorViewModel::toCamera)
    ) {
        Box(
            Modifier.align(Alignment.Center)
        ){
            BigPicture(-90f)
        }
        Box(
            Modifier.align(Alignment.BottomEnd)
        ){
            SendButton()
        }
    }
}