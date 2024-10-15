package com.github.picture2pc.desktop.ui.main.elements.sidebar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Data
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.ui.util.getIcon

@Composable
fun Header() {
    Row {
        Image(
            getIcon(Icons.Logo.STANDARD),
            "Logo",
            Modifier.width(75.dp)
        )
        Spacer(Modifier.width(Spacers.LARGE))

        Text(
            Data.APP_NAME,
            Modifier.align(Alignment.CenterVertically),
            style = TextStyles.HEADER1
        )
    }
}