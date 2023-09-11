package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ktx.jre.then
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.settings.impl.R

@Composable
internal fun SimpleElement(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    paddings: PaddingValues = PaddingValues(12.dp),
    titleTextStyle: TextStyle = LocalTypography.current.bodyR14
) {
    var rowModifier = Modifier
        .heightIn(min = 48.dp)
        .padding(paddings)
        .fillMaxWidth()
        .then(modifier)
    if (onClick != null) {
        rowModifier = Modifier
            .clickableRipple(onClick = onClick)
            .then(rowModifier)
    }
    Row(
        rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = titleTextStyle
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
            if (description != null) {
                Text(
                    text = description,
                    color = LocalPallet.current.text30,
                    style = LocalTypography.current.subtitleR12
                )
            }
        }
    }
}

@Composable
internal fun SimpleElement(
    modifier: Modifier = Modifier,
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    onClick: (() -> Unit)? = null,
    paddings: PaddingValues = PaddingValues(12.dp),
    titleTextStyle: TextStyle = LocalTypography.current.bodyR14
) {
    val title: String? = titleId?.let { stringResource(id = it) }
    val description = descriptionId?.let { stringResource(id = it) }

    SimpleElement(
        modifier = modifier,
        title = title,
        description = description,
        onClick = onClick,
        paddings = paddings,
        titleTextStyle = titleTextStyle
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableSimpleElementPreview() {
    SimpleElement(
        modifier = Modifier,
        titleId = R.string.experimental_screen_streaming,
        descriptionId = R.string.experimental_screen_streaming_desc
    )
}
