package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.TextStyles
import org.koin.compose.rememberKoinInject
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.github.picture2pc.android.R
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon

@Composable
fun BottomOfScreen(screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = screenSelectorViewModel::toGallery,
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(Colors.PRIMARY)
        ) {
            Icon(
                painter = painterResource(R.drawable.photo_library),
                contentDescription = "Gallery",
                modifier = Modifier.size(20.dp)
            )
        }
        Button(
            onClick = screenSelectorViewModel::toCamera,
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(Colors.PRIMARY)
        ) {
            Text(
                "Take Picture",
                style = TextStyles.NORMAL
            )
        }
    }
}