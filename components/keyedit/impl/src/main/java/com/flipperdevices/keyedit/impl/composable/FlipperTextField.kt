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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

private const val ANIMATION_DURATION_MS = 150

@Composable
fun FlipperTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    label: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    val textBoxStyle = LocalTypography.current.bodyR16

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
                    color = LocalPallet.current.text8,
                    style = LocalTypography.current.bodyR16
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences
            ),
            enabled = enabled
        )
        FlipperTextBoxUnderline(interactionSource)
    }
}

@Composable
private fun FlipperTitle(title: String) {
    Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = title,
        style = LocalTypography.current.bodyR16,
        color = LocalPallet.current.text30
    )
}

@Composable
private fun FlipperTextBox(
    text: String,
    label: @Composable () -> Unit,
    onTextChange: (String) -> Unit,
    interactionSource: MutableInteractionSource,
    textStyle: TextStyle,
    keyboardOptions: KeyboardOptions,
    enabled: Boolean
) {
    val focusManager = LocalFocusManager.current
    val decorationBox = @Composable { innerTextField: @Composable () -> Unit ->
        Box {
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
        textStyle = textStyle.copy(
            color = LocalPallet.current.text100
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        enabled = enabled,
        cursorBrush = SolidColor(LocalPallet.current.text100)
    )
}

@Composable
private fun FlipperTextBoxUnderline(
    interactionSource: InteractionSource
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val underlineColor = if (isFocused) {
        LocalPallet.current.accentSecond
    } else LocalPallet.current.text30
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
private fun ComposableFlipperTextFieldPreview() {
    FlipperTextField(
        title = "Name:",
        text = "",
        label = "Key_name",
        onTextChange = {}
    )
}
