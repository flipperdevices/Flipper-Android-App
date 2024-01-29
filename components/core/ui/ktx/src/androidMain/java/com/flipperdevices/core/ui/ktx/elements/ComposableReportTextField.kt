package com.flipperdevices.core.ui.ktx.elements

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComposableReportTextField(
    value: String,
    title: @Composable () -> Unit,
    placeholder: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    maxLines: Int? = null,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        title()
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = textFieldModifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .border(
                    width = 1.dp,
                    color = LocalPallet.current.reportBorder,
                    shape = RoundedCornerShape(8.dp)
                ),
            textStyle = LocalTypography.current.bodyR14.copy(
                color = LocalPallet.current.text100,
            ),
            enabled = enabled,
            singleLine = maxLines == 1,
            maxLines = maxLines ?: Int.MAX_VALUE,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.TextFieldDecorationBox(
                    placeholder = placeholder,
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = LocalPallet.current.text100,
                        focusedLabelColor = LocalPallet.current.text100.copy(alpha = ContentAlpha.high),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        backgroundColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    value = value,
                    visualTransformation = VisualTransformation.None,
                    innerTextField = innerTextField,
                    label = null,
                    leadingIcon = null,
                    trailingIcon = null,
                    singleLine = maxLines == 1,
                    enabled = enabled,
                    isError = false,
                    interactionSource = remember { MutableInteractionSource() },
                    contentPadding = PaddingValues(12.dp),
                )
            }
        )
    }
}
