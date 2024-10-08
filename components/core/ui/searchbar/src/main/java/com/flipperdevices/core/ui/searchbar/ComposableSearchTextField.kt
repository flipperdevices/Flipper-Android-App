package com.flipperdevices.core.ui.searchbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ComposableSearchTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit
) {
    SearchTextBox(
        text,
        label = { SearchTextLabel(hint) },
        onTextChange,
        LocalTypography.current.bodyR16,
        modifier
    )
}

@Composable
private fun SearchTextBox(
    text: String,
    label: @Composable () -> Unit,
    onTextChange: (String) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
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
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = text,
        onValueChange = onTextChange,
        decorationBox = decorationBox,
        textStyle = textStyle.copy(
            color = LocalPallet.current.text100
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
        }),
        cursorBrush = SolidColor(LocalPallet.current.text100)
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun SearchTextLabel(hint: String) {
    Text(
        text = hint,
        style = LocalTypography.current.bodyR16,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = LocalPallet.current.text40
    )
}
