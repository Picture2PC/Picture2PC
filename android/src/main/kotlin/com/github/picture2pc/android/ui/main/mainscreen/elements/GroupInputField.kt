package com.github.picture2pc.android.ui.main.mainscreen.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.github.picture2pc.android.viewmodel.mainscreenviewmodels.BroadcastViewModel
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.TextStyles
import org.koin.compose.rememberKoinInject

@Composable
fun GroupInputField(
    modifier: Modifier = Modifier,
    broadcastViewModel: BroadcastViewModel = rememberKoinInject(),
) {
    val focusManager = LocalFocusManager.current
    val groupName by broadcastViewModel.groupName.collectAsState()
    val localGroupName = remember { mutableStateOf(groupName) }

    LaunchedEffect(groupName) {
        localGroupName.value = groupName
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = localGroupName.value,
            onValueChange = {
                localGroupName.value = it
            },
            placeholder = { Text("Group Name") },
            label = { Text("Group") },
            modifier = Modifier
                .fillMaxWidth()
                .height(Heights.BUTTON + 10.dp),
            singleLine = true,
            shape = Shapes.BUTTON,
            textStyle = TextStyles.NORMAL,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrectEnabled = true,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                broadcastViewModel.setGroupName(localGroupName.value)
                focusManager.clearFocus()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Colors.PRIMARY,
                unfocusedBorderColor = Colors.PRIMARY,
                cursorColor = Colors.PRIMARY,
                focusedLabelColor = Colors.TEXT,
                unfocusedLabelColor = Colors.TEXT.copy(alpha = 0.8f),
            ),
        )
        
        if (groupName.isEmpty()) {
            Button(
                onClick = { 
                    broadcastViewModel.createNewGroup(localGroupName.value)
                    focusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Colors.PRIMARY,
                    contentColor = Colors.TEXT
                )
            ) {
                Text("Create Group", style = TextStyles.NORMAL)
            }
        }
    }
}
