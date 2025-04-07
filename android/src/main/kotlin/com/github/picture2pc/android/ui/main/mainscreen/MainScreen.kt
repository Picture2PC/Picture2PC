package com.github.picture2pc.android.ui.main.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.ui.main.mainscreen.elements.Banner
import com.github.picture2pc.android.ui.main.mainscreen.elements.BottomOfScreen
import com.github.picture2pc.android.ui.main.mainscreen.elements.ConnectableStateSwitch
import com.github.picture2pc.android.ui.main.mainscreen.elements.ConnectedClientsList
import com.github.picture2pc.android.ui.main.mainscreen.elements.NameInputField
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers

@Composable
fun VerticalMainScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Colors.SECONDARY, RoundedCornerShape(25.dp))
    ) {
        Row(
            Modifier
                .padding(25.dp)
                .fillMaxWidth()
        ) { Banner() }
        Row(
            Modifier
                .fillMaxSize()
                .background(Colors.BACKGROUND)
                .border(Borders.BORDER_THICK, Colors.PRIMARY, RoundedCornerShape(25.dp))
        ) {
            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row { NameInputField() }
                Spacer(Modifier.height(10.dp))
                ConnectableStateSwitch()
                Spacer(Modifier.height(10.dp))
                Row(Modifier.weight(1f)) { ConnectedClientsList(Modifier.fillMaxSize()) }
                Spacer(Modifier.height(10.dp))
                Row { BottomOfScreen() }
            }
        }
    }
}

@Composable
fun HorizontalMainScreen() {
    Row(
        Modifier
            .fillMaxSize()
            .background(Colors.SECONDARY, Shapes.MOBILE)
    ) {
        Column(
            Modifier
                .padding(20.dp + 10.dp)
                .fillMaxWidth(.5f)
                .fillMaxHeight()
        ) {
            Banner()
            Spacer(Modifier.height(Spacers.EXTRA_LARGE))
            NameInputField()
            Spacer(Modifier.height(Spacers.LARGE))
            ConnectableStateSwitch()
            Spacer(Modifier.weight(1f))
            BottomOfScreen()
        }
        Column(
            Modifier
                .align(Alignment.Top)
                .background(Colors.BACKGROUND, Shapes.MOBILE)
                .border(Borders.BORDER_THICK, Colors.PRIMARY, Shapes.MOBILE)
                .fillMaxSize()
        ) {
            Box(Modifier.padding(20.dp)) {
                ConnectedClientsList(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun MainScreen(isVertical: Boolean) {
    if (isVertical) VerticalMainScreen()
    else HorizontalMainScreen()
}