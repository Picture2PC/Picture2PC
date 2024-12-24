package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.github.picture2pc.common.ui.Borders
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.viewmodel.mainscreen.PictureDisplayViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun PopupNotification(pDVM: PictureDisplayViewModel = rememberKoinInject()) {
    val unseenPictures =
        pDVM.totalPictures.collectAsState().value - pDVM.selectedPictureIndex.collectAsState().value

    Box(
        modifier = Modifier
            .border(
                color = Colors.PRIMARY,
                width = Borders.BORDER_STANDARD,
                shape = Shapes.WINDOW
            )
            .background(color = Colors.SECONDARY.copy(.9f), shape = Shapes.WINDOW)
            .padding(Spacers.LARGE)
    ) {
        Text(
            "New Picture Received ($unseenPictures unseen pictures)",
            style = TextStyles.NORMAL
        )
    }
}