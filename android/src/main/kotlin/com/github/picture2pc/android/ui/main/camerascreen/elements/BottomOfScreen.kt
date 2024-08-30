package com.github.picture2pc.android.ui.main.camerascreen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.R
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.TextStyles
import org.koin.compose.rememberKoinInject

@Composable
fun BottomOfScreen(
    cameraViewModel: CameraViewModel = rememberKoinInject(),
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()
) {
    val flashMode = cameraViewModel.flashMode.collectAsState().value

    Row {
        IconButton(
            onClick = screenSelectorViewModel::toMain,
            modifier = Modifier,
            colors = IconButtonDefaults.iconButtonColors(Colors.PRIMARY)
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = "Back to main screen"
            )
        }
        Button(
            onClick = cameraViewModel::takeImage,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = Colors.BUTTON_PRIMARY
        ) {
            Text(text = "Take Picture", style = TextStyles.NORMAL)
        }
        IconButton(
            onClick = cameraViewModel::switchFlashMode,
            colors = IconButtonDefaults.iconButtonColors(Colors.PRIMARY)
        ) {
            Icon(
                painter = painterResource(flashMode.resourceInt),
                contentDescription = "Change flash mode"
            )
        }
    }
}