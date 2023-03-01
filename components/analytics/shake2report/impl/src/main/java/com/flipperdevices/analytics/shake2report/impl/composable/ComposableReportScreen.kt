package com.flipperdevices.analytics.shake2report.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.analytics.shake2report.impl.R
import com.flipperdevices.core.ui.ktx.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet

private const val MAX_TITLE = 256

@Composable
fun ComposableReportScreen(
    modifier: Modifier = Modifier,
    onSubmit: (title: String, desc: String) -> Unit
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = name,
        label = { Text(stringResource(R.string.shake2report_name)) },
        onValueChange = { name = it.take(MAX_TITLE) },
        maxLines = 1,
        placeholder = {
            Text(stringResource(R.string.shake2report_name_placeholder))
        },
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = LocalPallet.current.text100,
            focusedLabelColor = LocalPallet.current.text100.copy(alpha = ContentAlpha.high)
        ),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 16.dp),
        value = description,
        label = { Text(stringResource(R.string.shake2report_description)) },
        onValueChange = { description = it },
        placeholder = {
            Text(stringResource(R.string.shake2report_description_placeholder))
        },
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = LocalPallet.current.text100,
            focusedLabelColor = LocalPallet.current.text100.copy(alpha = ContentAlpha.high)
        ),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )
    ComposableFlipperButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        text = stringResource(R.string.shake2report_btn),
        onClick = { onSubmit(name, description) }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableComposableReportScreenPreview() {
    FlipperThemeInternal {
        ComposableReportScreen(
            onSubmit = { _, _ -> }
        )
    }
}
