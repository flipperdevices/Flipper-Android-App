package com.flipperdevices.keyedit.impl.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem

private const val ANIMATION_DURATION_MS = 150

@Composable
@Suppress("LongParameterList")
fun FlipperTextField(
    modifier: Modifier = Modifier,
    title: String? = null,
    label: String? = null,
    text: String,
    onTextChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val textBoxStyle = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        color = colorResource(DesignSystem.color.black_100)
    )

    Column(modifier) {
        if (title != null) {
            FlipperTitle(title)
        }
        FlipperTextBox(
            text = text,
            onTextChange = onTextChange,
            interactionSource = interactionSource,
            textStyle = textBoxStyle,
            label = {
                if (label == null) {
                    return@FlipperTextBox
                }
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    color = colorResource(DesignSystem.color.black_8)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done
            )
        )
        FlipperTextBoxUnderline(interactionSource)
    }
}

@Composable
private fun FlipperTitle(title: String) {
    Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = title,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        color = colorResource(DesignSystem.color.black_30)
    )
}

@Composable
@Suppress("LongParameterList")
private fun FlipperTextBox(
    text: String,
    label: @Composable () -> Unit,
    onTextChange: (String) -> Unit,
    interactionSource: MutableInteractionSource,
    textStyle: TextStyle,
    keyboardOptions: KeyboardOptions,
) {
    val focusManager = LocalFocusManager.current
    val decorationBox = @Composable { innerTextField: @Composable () -> Unit ->
        Box() {
            innerTextField()
            if (text.isEmpty()) {
                label()
            }
        }
    }

    BasicTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = text,
        onValueChange = onTextChange,
        interactionSource = interactionSource,
        decorationBox = decorationBox,
        textStyle = LocalTextStyle.current.merge(textStyle),
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        })
    )
}

@Composable
private fun FlipperTextBoxUnderline(
    interactionSource: InteractionSource
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val underlineColor = if (isFocused) {
        colorResource(DesignSystem.color.accent_secondary)
    } else colorResource(DesignSystem.color.black_30)
    val underlineColorAnimated by animateColorAsState(
        underlineColor,
        tween(durationMillis = ANIMATION_DURATION_MS)
    )
    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = underlineColorAnimated
    )
}

@Composable
@Preview(
    showSystemUi = true,
    showBackground = true
)
@Suppress("UnusedPrivateMember")
private fun FlipperTextFieldPreview() {
    FlipperTextField(
        title = "Name:",
        text = "",
        label = "Key_name",
        onTextChange = {}
    )
}
