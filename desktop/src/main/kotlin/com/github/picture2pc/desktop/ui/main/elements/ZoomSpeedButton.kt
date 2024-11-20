package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.desktop.ui.constants.Descriptions
import com.github.picture2pc.desktop.viewmodel.mainscreen.MovementHandlerViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ZoomSpeedButton(mDVM: MovementHandlerViewModel = rememberKoinInject()) {
    val draggingSpeed = mDVM.draggingSpeed.collectAsState()

    TooltipIconButton(
        description = Descriptions.DRAGGING_SPEED,
        icon = draggingSpeed.value.iconPath,
        color = Colors.ACCENT,
        onClick = mDVM::updateDraggingSpeed
    )
}