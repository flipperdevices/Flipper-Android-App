package com.flipperdevices.archive.search.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.flipperdevices.archive.search.R
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ComposableSearchTextField(
    modifier: Modifier,
    text: String,
    onTextChange: (String) -> Unit
) {
    SearchTextBox(
        modifier,
        text,
        label = { SearchTextLabel() },
        onTextChange,
        LocalTypography.current.bodyR16
    )
}

@Composable
private fun SearchTextBox(
    modifier: Modifier,
    text: String,
    label: @Composable () -> Unit,
    onTextChange: (String) -> Unit,
    textStyle: TextStyle
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
        textStyle = LocalTextStyle.current.merge(textStyle),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            autoCorrect = false
        ),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
        })
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun SearchTextLabel() {
    Text(
        text = stringResource(R.string.search_field_hint),
        style = LocalTypography.current.bodyR16,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = LocalPallet.current.text40
    )
}
