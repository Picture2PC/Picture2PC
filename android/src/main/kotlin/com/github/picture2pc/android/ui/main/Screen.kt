package com.github.picture2pc.android.ui.main


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.picture2pc.android.ui.main.MainScreen.MainScreen
import com.github.picture2pc.android.ui.theme.Picture2PcTheme


@Composable
@Preview
fun Screen(){
    Picture2PcTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

                MainScreen()

        }
    }
}