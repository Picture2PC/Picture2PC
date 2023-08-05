package com.github.picture2pc.android.ui.main.MainScreen.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Progress(modifier: Modifier){
    Column(modifier = modifier){
        Text(
            fontSize = 20.sp, text = "Sent:", modifier = Modifier
                .fillMaxWidth()
        )
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp), progress = 0.5F
        )
    }
}