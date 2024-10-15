package com.github.picture2pc.android.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.bigpicturescreen.HorizontalBigPictureScreen
import com.github.picture2pc.android.ui.main.bigpicturescreen.VerticalBigPictureScreen
import com.github.picture2pc.android.ui.main.camerascreen.HorizontalCameraScreen
import com.github.picture2pc.android.ui.main.camerascreen.VerticalCameraScreen
import com.github.picture2pc.android.ui.main.mainscreen.HorizontalMainScreen
import com.github.picture2pc.android.ui.main.mainscreen.VerticalMainScreen
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import androidx.compose.ui.platform.LocalContext
import com.github.picture2pc.common.ui.Shapes
import org.koin.compose.rememberKoinInject


@Composable
fun Screen(vertical: Boolean, screenSelector: ScreenSelectorViewModel = rememberKoinInject()) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    MaterialTheme(darkColorScheme()) {
        Surface(
            color = Colors.BACKGROUND,
            modifier = Modifier
                .clickable(
                    onClick = focusManager::clearFocus,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                )
        ) {
            Box(
                Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .border(Borders.BORDER_THICK, Colors.PRIMARY, Shapes.MOBILE)
            ) {
                when (screenSelector.value) {
                    ScreenSelectorViewModel.Screens.MAIN ->
                        if (vertical) VerticalMainScreen()
                        else HorizontalMainScreen()

                    ScreenSelectorViewModel.Screens.CAMERA ->
                        if (vertical) VerticalCameraScreen()
                        else HorizontalCameraScreen()

                    ScreenSelectorViewModel.Screens.BIG_PICTURE ->
                        if (vertical) VerticalBigPictureScreen()
                        else HorizontalBigPictureScreen()

                    ScreenSelectorViewModel.Screens.GALLERY ->
                        screenSelector.openGallery(context)
                }
            }
        }
    }
}