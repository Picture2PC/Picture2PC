package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.desktop.ui.constants.Descriptions
import com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ZoomSpeedButton(pDVM: PictureDisplayViewModel = rememberKoinInject()) {
    val draggingSpeed = remember { pDVM.movementHandler.draggingSpeed }

    TooltipIconButton(
        description = Descriptions.DRAGGING_SPEED,
        icon = draggingSpeed.value.iconPath,
        color = Colors.ACCENT,
    ) {
        draggingSpeed.value = draggingSpeed.value.next()
    }
}