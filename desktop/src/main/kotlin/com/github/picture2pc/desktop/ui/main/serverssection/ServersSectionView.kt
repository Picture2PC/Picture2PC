package com.github.picture2pc.desktop.ui.main.serverssection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.picture2pc.desktop.viewmodel.serversectionviewmodel.ServersSectionViewModel
import org.koin.compose.rememberKoinInject

@Composable
fun ServersSectionView(
    modifier: Modifier = Modifier,
) {
    val viewModel: ServersSectionViewModel = rememberKoinInject()

    Surface(
        color = Color.LightGray,
        modifier = Modifier.then(modifier)
    ) {
        Box {
            ServerList(viewModel)
            Button(
                onClick = viewModel::refreshServers,
                colors = ButtonDefaults.buttonColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Text("Refresh")
            }
        }
    }
}
