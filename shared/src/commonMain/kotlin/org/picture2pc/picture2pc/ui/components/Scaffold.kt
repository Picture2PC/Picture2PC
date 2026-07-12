package org.picture2pc.picture2pc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import org.picture2pc.picture2pc.ui.theme.Padding
import org.picture2pc.picture2pc.ui.theme.Picture2PCTheme
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.Theme

@Composable
fun AppScaffold(
    theme: Theme = Theme.Dark,
    content: @Composable BoxScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current
    Picture2PCTheme(theme = theme) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Colors.background)
                .displayCutoutPadding()
                .pointerInput(Unit) {
                    detectTapGestures { focusManager.clearFocus() }
                },
            content = content
        )
    }
}

@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable BoxWithConstraintsScope.(isLandscape: Boolean) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val isLandscape = maxWidth > maxHeight
        content(isLandscape)
    }
}

@Composable
fun AdaptiveSplitScreen(
    modifier: Modifier = Modifier,
    landscapeLeft: @Composable ColumnScope.(isLandscape: Boolean) -> Unit,
    landscapeRight: @Composable ColumnScope.() -> Unit,
    portrait: @Composable ColumnScope.(isLandscape: Boolean) -> Unit
) {
    AppScaffold {
        AdaptiveLayout(modifier = modifier.fillMaxWidth()) { isLandscape ->
            ScreenCardContainer {
                if (isLandscape) {
                    Row {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(Padding.Container)
                                .weight(1f)
                        ) {
                            landscapeLeft(true)
                        }

                        InnerCard(Modifier.weight(1f)) {
                            landscapeRight()
                        }
                    }
                } else {
                    portrait(false)
                }
            }
        }
    }
}