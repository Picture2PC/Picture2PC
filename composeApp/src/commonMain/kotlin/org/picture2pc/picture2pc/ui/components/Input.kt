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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.picture2pc.picture2pc.ui.theme.PictureTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PictureInput(
    label: String,
    value: String,
    isError: Boolean = false,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        interactionSource = interactionSource,
        singleLine = true,
        textStyle = TextStyle(color = PictureTheme.colors.text),
        cursorBrush = SolidColor(PictureTheme.colors.primary),
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
                enabled = enabled,
                singleLine = true,
                isError = isError,
                label = { Text(text = label, style = PictureTheme.typography.labelLarge) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PictureTheme.colors.text,
                    unfocusedTextColor = PictureTheme.colors.text,
                    disabledTextColor = PictureTheme.colors.textDisabled,
                    errorTextColor = PictureTheme.colors.errorBright,
                    focusedContainerColor = PictureTheme.colors.background,
                    unfocusedContainerColor = PictureTheme.colors.background,
                    disabledContainerColor = PictureTheme.colors.background,
                    errorContainerColor = PictureTheme.colors.background,
                    focusedLabelColor = PictureTheme.colors.text,
                    unfocusedLabelColor = PictureTheme.colors.text,
                    disabledLabelColor = PictureTheme.colors.textDisabled,
                    errorLabelColor = PictureTheme.colors.errorBright,
                    focusedPlaceholderColor = PictureTheme.colors.textDisabled,
                    unfocusedPlaceholderColor = PictureTheme.colors.textDisabled,
                    disabledPlaceholderColor = PictureTheme.colors.textDisabled,
                    errorPlaceholderColor = PictureTheme.colors.errorBright,
                    focusedBorderColor = PictureTheme.colors.primary,
                    unfocusedBorderColor = PictureTheme.colors.primary,
                    disabledBorderColor = PictureTheme.colors.primaryDisabled,
                    errorBorderColor = PictureTheme.colors.error,
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
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PictureTheme.colors.primary,
                            unfocusedBorderColor = PictureTheme.colors.primary,
                            disabledBorderColor = PictureTheme.colors.primaryDisabled,
                            errorBorderColor = PictureTheme.colors.error,
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
