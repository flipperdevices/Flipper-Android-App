package com.flipperdevices.archive.search.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.flipperdevices.archive.search.R
import com.flipperdevices.core.ui.R as DesignSystem

@Composable
internal fun ComposableSearchTextField(
    modifier: Modifier,
    text: String,
    onTextChange: (String) -> Unit,
) {
    SearchTextBox(
        modifier,
        text,
        label = { SearchTextLabel() },
        onTextChange,
        TextStyle(
            color = colorResource(DesignSystem.color.black_100),
            fontWeight = FontWeight.W400,
            fontSize = 16.sp
        )
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

    BasicTextField(
        modifier = modifier,
        value = text,
        onValueChange = onTextChange,
        decorationBox = decorationBox,
        textStyle = LocalTextStyle.current.merge(textStyle),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
        })
    )
}

@Composable
private fun SearchTextLabel() {
    Text(
        text = stringResource(R.string.search_field_hint),
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = colorResource(DesignSystem.color.black_40)
    )
}
