package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.picture2pc.android.R
import com.github.picture2pc.common.ui.TextStyles

@Composable
fun Banner() {
    Column(Modifier.height(100.dp)) {
        Image(painterResource(R.drawable.app_icon), "Logo")
    }
    Spacer(Modifier.width(20.dp))
    Column {
        Box(
            Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Picture2PC",
                Modifier.align(Alignment.Center),
                style = TextStyles.HEADER1.copy(fontSize = 32.sp)
            )
        }
    }
}