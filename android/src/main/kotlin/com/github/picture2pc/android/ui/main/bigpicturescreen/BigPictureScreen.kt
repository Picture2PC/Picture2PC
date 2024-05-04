package com.github.picture2pc.android.ui.main.bigpicturescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.android.ui.main.bigpicturescreen.elements.BigPicture
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun BigPictureScreen(screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
) {
    Box(Modifier
        .fillMaxSize()
        .clickable ( onClick =  screenSelectorViewModel::toCamera )
    ){
        BigPicture()
    }
}