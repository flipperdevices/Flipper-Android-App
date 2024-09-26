package com.flipperdevices.filemanager.ui.components.name

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

private const val ANIMATION_DURATION_MS = 150

@Composable
internal fun FlipperTextField(
    text: String,
    title: String,
    subtitle: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val textBoxStyle = LocalTypography.current.bodyR16

    Column(modifier) {
        Text(
            text = title,
            color = animateColorAsState(
                targetValue = when (isError) {
                    true -> LocalPalletV2.current.text.semantic.danger
                    false -> LocalPalletV2.current.text.label.secondary
                }
            ).value,
            style = LocalTypography.current.bodyM14
        )
        Spacer(Modifier.height(8.dp))
        FlipperTextBox(
            text = text,
            onTextChange = onTextChange,
            interactionSource = interactionSource,
            textStyle = textBoxStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences
            ),
            enabled = enabled
        )
        Spacer(Modifier.height(8.dp))
        FlipperTextBoxUnderline(
            interactionSource = interactionSource,
            isError = isError
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            color = animateColorAsState(
                targetValue = when (isError) {
                    true -> LocalPalletV2.current.text.semantic.danger
                    false -> LocalPalletV2.current.text.label.secondary
                }
            ).value,
            style = LocalTypography.current.subtitleM10
        )
    }
}

@Composable
private fun FlipperTextBox(
    text: String,
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
        }
    }

    BasicTextField(
        modifier = Modifier.fillMaxWidth(),
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
    interactionSource: InteractionSource,
    isError: Boolean
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    val underlineColorAnimated by animateColorAsState(
        targetValue = when {
            isError -> LocalPalletV2.current.text.semantic.danger
            isFocused -> LocalPallet.current.accentSecond
            else -> LocalPallet.current.text30
        },
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS)
    )
    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = underlineColorAnimated
    )
}
