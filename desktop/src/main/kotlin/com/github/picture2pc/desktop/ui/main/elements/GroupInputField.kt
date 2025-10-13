package com.github.picture2pc.desktop.ui.main.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.github.picture2pc.common.ui.Colors
import com.github.picture2pc.common.ui.Heights
import com.github.picture2pc.common.ui.Shapes
import com.github.picture2pc.common.ui.TextStyles
import com.github.picture2pc.desktop.viewmodel.mainscreen.BroadcastViewModel
import org.koin.compose.rememberKoinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun GroupInputField(
    focusManager: FocusManager,
    broadcastViewModel: BroadcastViewModel = rememberKoinInject()
) {
    val groupName = remember { mutableStateOf(broadcastViewModel.getGroupName()) }
    val submitKeys = setOf(Key.Enter, Key.NumPadEnter)

    Column {
        OutlinedTextField(
            value = groupName.value,
            onValueChange = { groupName.value = it },
            placeholder = { Text("Group Name") },
            label = { Text("Group") },
            modifier = Modifier
                .fillMaxWidth()
                .height(Heights.BUTTON + 10.dp)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key in submitKeys) {
                        broadcastViewModel.setGroupName(groupName.value)
                        focusManager.clearFocus()
                    }
                    true
                },
            singleLine = true,
            shape = Shapes.BUTTON,
            textStyle = TextStyles.NORMAL,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Colors.PRIMARY,
                unfocusedBorderColor = Colors.PRIMARY,
                cursorColor = Colors.PRIMARY,
                focusedLabelColor = Colors.TEXT,
                unfocusedLabelColor = Colors.TEXT.copy(alpha = 0.8f),
            ),
        )

        if (broadcastViewModel.getGroupUuid().isEmpty()) {
            Button(
                onClick = {
                    broadcastViewModel.createNewGroup(groupName.value)
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
