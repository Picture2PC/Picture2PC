package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StateInfoPictureButton(modifier: Modifier) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(0.6F),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(fontSize = 20.sp, text = "State:")
            Spacer(modifier = Modifier.width(10.dp))
            Text(fontSize = 20.sp, text = "Idle")
        }
        Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(5.dp)) {
            Text(text = "TakePicture")
        }
    }
}