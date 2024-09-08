package com.github.picture2pc.android.ui.main.bigpicturescreen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.github.picture2pc.android.R
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.TextStyles
import org.koin.compose.rememberKoinInject

@Composable
fun BottomOfScreen(
    screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject(),
    cameraViewModel: CameraViewModel = rememberKoinInject()
) {
    Row {
        IconButton(
            onClick = screenSelectorViewModel::toMain,
            colors = IconButtonDefaults.iconButtonColors(Colors.PRIMARY)
        ) {
            Icon(
                painter = painterResource(R.drawable.home),
                contentDescription = "Home",
                tint = Colors.TEXT
            )
        }
        Button(
            onClick = cameraViewModel::sendImage,
            colors = Colors.BUTTON_PRIMARY,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Send", style = TextStyles.NORMAL)
        }
    }
}