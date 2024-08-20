package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.picture2pc.android.R

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
            painter = painterResource(id = R.drawable.icon),
            contentDescription = "ICON",
            modifier = Modifier
                .fillMaxWidth()
                .scale(.8F)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(8.dp)
        )
    }
}