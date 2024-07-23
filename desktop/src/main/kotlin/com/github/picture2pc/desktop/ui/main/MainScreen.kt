package com.github.picture2pc.desktop.ui.main

import QualitySelector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.ui.main.connectivitybuttons.ReloadButton
import com.github.picture2pc.desktop.ui.main.imagemanupulation.ImageManipulationButtons
import com.github.picture2pc.desktop.ui.main.picturedisplay.Picture

val shape = RoundedCornerShape(20)

@Composable
fun MainScreen() {
    Box(Modifier.fillMaxSize()){
        Column {
            Picture()
        }
        Column(Modifier
            .fillMaxHeight()
            .background(Color.LightGray)
            .align(Alignment.TopEnd)
            .padding(10.dp)
            .width(228.dp)
        ) {
            Row { QualitySelector() }
            Row { ImageManipulationButtons() }
            Row { ReloadButton() }
        }
    }
}