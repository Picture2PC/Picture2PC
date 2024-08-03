package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.picture2pc.common.ui.Icons
import com.github.picture2pc.common.ui.getIcon

@Composable
fun BrandingTopBar(modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            fontSize = 24.sp,
            text = "Picture2PC",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.6F)
        )
        Image(
            painter = getIcon(path = Icons.Logo.STANDARD),
            contentDescription = "ICON",
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}