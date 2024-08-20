package com.github.picture2pc.desktop.ui.main.serverssection

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Spacers
import com.github.picture2pc.common.ui.StateColors
import com.github.picture2pc.common.ui.Style
import com.github.picture2pc.common.ui.TextStyles

@Composable
fun connectionInfo() {
    Column {
        Text(
            "Connections",
            Modifier.padding(Spacers.NORMAL),
            Colors.TEXT,
            style = TextStyles.HEADER2
        )

        //TODO: Replace with actual connection info
        connection("Joe Mama", "Receiving")
        connection("Joe Papa", "Connected")
        Spacer(Modifier.height(Spacers.NORMAL))
    }
}

@Composable
//TODO: Replace state with actual connection state
fun connection(name: String, state: String) {
    Row(Modifier.padding(start = Spacers.NORMAL, end = Spacers.NORMAL, top = Spacers.SMALL)) {
        Text(name, color = Colors.TEXT, style = TextStyles.NORMAL)

        Text(state, color = Colors.TEXT, style = TextStyles.NORMAL)
        Canvas(Modifier.size(Style.Dimensions.StateIndicator)) {
            drawCircle(StateColors.CONNECTED)
        }
    }
}
