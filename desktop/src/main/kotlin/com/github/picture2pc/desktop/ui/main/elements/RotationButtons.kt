package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.desktop.data.next
import com.github.picture2pc.desktop.ui.constants.Descriptions
import com.github.picture2pc.desktop.viewmodel.mainscreen.MovementHandlerViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun RotationButtons(
    mDVM: MovementHandlerViewModel = rememberKoinInject()
) {
    Row {
        TooltipIconButton(
            description = Descriptions.ROTATE_LEFT,
            icon = Icons.Desktop.ROTATE_LEFT,
            color = Colors.ACCENT,
        ) {
            mDVM.rotationState.value =
                mDVM.rotationState.value.next(false)
        }
        Spacer(Modifier.width(Spacers.SMALL))

        TooltipIconButton(
            description = Descriptions.ROTATE_RIGHT,
            icon = Icons.Desktop.ROTATE_RIGHT,
            color = Colors.ACCENT,
        ) {
            mDVM.rotationState.value =
                mDVM.rotationState.value.next(true)
        }
    }
}