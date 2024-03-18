package com.github.picture2pc.android.ui.main.camerascreen.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val shape = RoundedCornerShape(5.dp)

@Composable
fun TakePictureButton(
    onClickReturn: () -> Unit,
    onClickPicture: () -> Unit,
    onClickSend: () -> Unit
    ){
    Row (
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f)).padding(20.dp)
    ) {
        Column( modifier = Modifier.weight(.5f, true), horizontalAlignment = Alignment.Start ){
            IconButton(onClick = onClickReturn) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
        }
        Column(horizontalAlignment = Alignment.End ){
            Button(onClick = onClickPicture, shape = shape) {
                Text(text = "Take Picture")
            }
            Button(onClick = onClickSend, shape = shape) {
                Text(text = "Send")
            }
        }
    }
}