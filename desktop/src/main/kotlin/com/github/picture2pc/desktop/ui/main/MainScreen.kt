package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.picture.Picture
import com.github.picture2pc.desktop.ui.main.serverssection.ServersSectionView

@Composable
fun MainScreen() {
    Row {
        BoxWithConstraints {
            ServersSectionView(
                modifier = Modifier
                    .fillMaxHeight()
                    .width((maxWidth * 0.2f).coerceAtLeast(300.dp))
            )
        }
        BoxWithConstraints {
            Picture(
                modifier = Modifier
                    .fillMaxHeight()
            )
        }
    }
}
