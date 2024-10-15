package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.github.picture2pc.android.R
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.TextStyles
import org.koin.compose.rememberKoinInject

@Composable
fun BottomOfScreen(screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()) {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = screenSelectorViewModel::toGallery,
            modifier = Modifier,
            colors = IconButtonDefaults.iconButtonColors(Colors.PRIMARY)
        ) {
            Icon(
                painter = painterResource(R.drawable.photo_library),
                contentDescription = "Gallery",
                tint = Colors.TEXT
            )
        }
        Button(
            onClick = screenSelectorViewModel::toCamera,
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(Colors.PRIMARY),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Take Picture",
                style = TextStyles.NORMAL
            )
        }
    }
}