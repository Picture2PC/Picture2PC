package org.picture2pc.picture2pc.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Colors
import org.picture2pc.picture2pc.ui.theme.PictureTheme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PictureInput(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    placeholder: String,
    isError: Boolean = false,
    disabled: Boolean = false,
    onValueChange: (String) -> Unit = {},
    onDone: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        enabled = !disabled,
        interactionSource = interactionSource,
        singleLine = true,
        textStyle = TextStyle(color = Colors.text),
        cursorBrush = SolidColor(Colors.primary),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onDone()
            }
        ),
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = !disabled,
                singleLine = true,
                isError = isError,
                label = { Text(text = label, style = Typography.labelLarge) },
                placeholder = { Text(text = placeholder, style = Typography.labelLarge) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Colors.text,
                    unfocusedTextColor = Colors.text,
                    disabledTextColor = Colors.textDisabled,
                    errorTextColor = Colors.errorBright,
                    focusedContainerColor = Colors.background,
                    unfocusedContainerColor = Colors.background,
                    disabledContainerColor = Colors.background,
                    errorContainerColor = Colors.background,
                    focusedLabelColor = Colors.text,
                    unfocusedLabelColor = Colors.text,
                    disabledLabelColor = Colors.textDisabled,
                    errorLabelColor = Colors.errorBright,
                    focusedPlaceholderColor = Colors.textDisabled,
                    unfocusedPlaceholderColor = Colors.textDisabled,
                    disabledPlaceholderColor = Colors.textDisabled,
                    errorPlaceholderColor = Colors.errorBright,
                    focusedBorderColor = Colors.primary,
                    unfocusedBorderColor = Colors.primary,
                    disabledBorderColor = Colors.primaryDisabled,
                    errorBorderColor = Colors.error,
                ),
                interactionSource = interactionSource,
                visualTransformation = VisualTransformation.None,
                contentPadding = PaddingValues(
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = !disabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Colors.primary,
                            unfocusedBorderColor = Colors.primary,
                            disabledBorderColor = Colors.primaryDisabled,
                            errorBorderColor = Colors.error,
                        ),
                        shape = RoundedCornerShape(25.dp),
                        focusedBorderThickness = 2.5.dp,
                        unfocusedBorderThickness = 1.5.dp,
                    )
                }
            )
        }
    )
}
