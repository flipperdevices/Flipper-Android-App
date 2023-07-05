package com.flipperdevices.analytics.shake2report.impl.composable.states

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.analytics.shake2report.impl.R
import com.flipperdevices.analytics.shake2report.impl.composable.components.ComposableCheckBox
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.ktx.elements.ComposableReportTextField
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

private const val MAX_TITLE = 256

@Composable
internal fun ComposableReportPending(
    modifier: Modifier = Modifier,
    onSubmit: (title: String, desc: String, addLogs: Boolean) -> Unit
) = Column(
    modifier = modifier
        .padding(horizontal = 16.dp)
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(true) }

    ComposableReportPendingFields(
        modifier = Modifier.weight(1f),
        name = name,
        description = description,
        checked = checked,
        onChangeName = { name = it.take(MAX_TITLE) },
        onChangeDescription = { description = it },
        onChangeChecked = { checked = it }
    )

    ComposableFlipperButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        text = stringResource(R.string.shake2report_btn),
        onClick = { onSubmit(name, description, checked) },
        enabled = name.isNotEmpty() && description.isNotEmpty()
    )
}

@Composable
@Suppress("LongParameterList")
private fun ComposableReportPendingFields(
    name: String,
    description: String,
    checked: Boolean,
    onChangeName: (String) -> Unit,
    onChangeDescription: (String) -> Unit,
    onChangeChecked: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .padding(bottom = 16.dp)
        .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    val placeHolderStyle = LocalTypography.current.bodyR14.copy(
        color = LocalPallet.current.reportBorder
    )
    val titleStyle = LocalTypography.current.buttonB16.copy(
        color = LocalPallet.current.text100
    )

    ComposableReportTextField(
        value = name,
        title = {
            Text(
                text = stringResource(R.string.shake2report_name),
                style = titleStyle,
            )
        },
        placeholder = {
            Text(
                text = stringResource(R.string.shake2report_name_placeholder),
                style = placeHolderStyle
            )
        },
        maxLines = 1,
        onValueChange = onChangeName
    )

    ComposableReportTextField(
        value = description,
        title = {
            Text(
                text = stringResource(R.string.shake2report_description),
                style = titleStyle,
            )
        },
        placeholder = {
            Text(
                text = stringResource(R.string.shake2report_description_placeholder),
                style = placeHolderStyle
            )
        },
        onValueChange = onChangeDescription,
        modifier = Modifier.height(250.dp)
    )

    ComposableReportAddLog(checked = checked, onCheckedChange = onChangeChecked)
}

@Composable
private fun ComposableReportAddLog(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableCheckBox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = stringResource(R.string.shake2report_add_log),
            style = LocalTypography.current.bodyM14.copy(
                color = LocalPallet.current.accent
            ),
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableComposableReportScreenPreview() {
    FlipperThemeInternal {
        ComposableReportPending(
            onSubmit = { _, _, _ -> }
        )
    }
}
