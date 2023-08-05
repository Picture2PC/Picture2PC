package com.github.picture2pc.android.ui.main.MainScreen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.picture2pc.android.viewmodel.MainScreenViewModels
import org.koin.compose.rememberKoinInject

@Composable
fun Connection(modifier: Modifier){
    val viewModel: MainScreenViewModels.BroadcastViewModel = rememberKoinInject()
    val serving = viewModel.serving.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
    ) {
        Text(
            fontSize = 20.sp,
            text = "Connectable:",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.5F)
        )
        Switch(checked = serving.value, onCheckedChange = { viewModel.checkedChanged(it)}, modifier = Modifier.fillMaxWidth())
    }
}