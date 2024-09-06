package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.screenselectorviewmodels.ScreenSelectorViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun BottomOfScreen(screenSelectorViewModel: ScreenSelectorViewModel = rememberKoinInject()) {
    Button(
        onClick = screenSelectorViewModel::toCamera,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp)
    ) {
        Text(
            "Take Picture",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}