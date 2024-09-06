package com.github.picture2pc.android.ui.main.mainscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.R
import com.github.picture2pc.android.ui.main.mainscreen.elements.BottomOfScreen
import com.github.picture2pc.android.ui.main.mainscreen.elements.ConnectableStateSwitch
import com.github.picture2pc.android.ui.main.mainscreen.elements.ConnectedClientsList
import com.github.picture2pc.android.ui.main.mainscreen.elements.ServerNameInputField

@Composable
fun MainScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(25.dp))
    ) {
        Row(
            Modifier
                .padding(40.dp)
                .fillMaxWidth()
        ) {
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
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
        Row(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(25.dp))
        ) {
            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row { ServerNameInputField() }
                Spacer(Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        ConnectableStateSwitch()
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "Connectable",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.weight(1f)) {
                    ConnectedClientsList(Modifier.fillMaxSize())
                }
                Spacer(Modifier.height(10.dp))
                Row {
                    BottomOfScreen()
                }
            }
        }
    }
}