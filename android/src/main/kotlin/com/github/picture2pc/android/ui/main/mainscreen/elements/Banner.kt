package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.picture2pc.android.R
import com.github.picture2pc.common.ui.Style

@Composable
fun Banner() {
    Row(Modifier.height(100.dp)) {
        Image(painterResource(R.drawable.app_icon), "Logo")
        Spacer(Modifier.weight(1f))
        Text(
            "Picture2PC",
            style = Style.TextStyles.HEADER1.copy(fontSize = 32.sp),
            modifier = Modifier.align(
                Alignment.CenterVertically
            )
        )
        Spacer(Modifier.weight(1f))
    }
}