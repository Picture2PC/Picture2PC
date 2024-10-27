package com.github.picture2pc.desktop.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.desktop.extention.transpose
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.ui.main.elements.Picture
import com.github.picture2pc.desktop.ui.main.elements.RotationButtons
import com.github.picture2pc.desktop.ui.main.elements.Sidebar
import com.github.picture2pc.desktop.ui.main.elements.ZoomSpeedButton
import com.github.picture2pc.desktop.viewmodel.mainscreen.MovementHandlerViewModel
import com.github.picture2pc.desktop.viewmodel.mainscreen.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun MainScreen(
    mHVM: MovementHandlerViewModel = rememberKoinInject(),
    pDVM: PictureDisplayViewModel = rememberKoinInject()
) {
    var withAndHeight by remember { mutableStateOf(DpSize.Zero) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Colors.BACKGROUND)
    ) {
        Row(
            Modifier.padding(10.dp).fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .width(Settings.SIDEBAR_WIDTH.dp)
                    .background(Colors.SECONDARY, Shapes.WINDOW)
            ) { Sidebar() }
            Spacer(Modifier.width(Spacers.NORMAL))

            Box(Modifier.fillMaxSize()) {
                // Picture Display
                Box(
                    Modifier.border(
                        Borders.BORDER_STANDARD,
                        Colors.PRIMARY,
                        Shapes.WINDOW
                    ).onGloballyPositioned {
                        withAndHeight = DpSize(it.size.width.dp, it.size.height.dp)
                        pDVM.calculateRatio(it.size.toSize())
                    }.fillMaxSize(),
                    Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(withAndHeight.transpose(mHVM.rotationState.value))
                            .padding(Spacers.NORMAL),
                        Alignment.Center
                    ) { Picture() }
                }

                Row(Modifier.padding(Spacers.NORMAL)) {
                    RotationButtons()
                    Spacer(Modifier.weight(1f))
                    ZoomSpeedButton()
                }
            }
        }
    }
}