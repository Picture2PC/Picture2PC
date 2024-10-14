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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon

@Composable
fun BottomOfScreen(screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = screenSelectorViewModel::toGallery,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(Colors.PRIMARY),
            modifier = Modifier.weight(0.3f)
        ) {
            Icon(
                painter = painterResource(R.drawable.photo_library),
                contentDescription = "Gallery",
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.size(6.dp))
        Button(
            onClick = screenSelectorViewModel::toCamera,
            modifier = Modifier.fillMaxWidth().weight(1f),
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