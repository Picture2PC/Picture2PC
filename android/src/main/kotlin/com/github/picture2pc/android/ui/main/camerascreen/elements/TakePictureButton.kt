package com.github.picture2pc.android.ui.main.camerascreen.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val shape = RoundedCornerShape(5.dp)

@Composable
fun TakePictureButton(modifier: Modifier, onClick: () -> Unit){
    Column(modifier = modifier, horizontalAlignment = Alignment.End){
        Button(onClick = { /*TODO*/ }, shape = shape) {
            Text(text = "Take Picture")
        }
        Button(onClick = onClick, shape = shape) {
            Text(text = "Send")
        }
    }
}
