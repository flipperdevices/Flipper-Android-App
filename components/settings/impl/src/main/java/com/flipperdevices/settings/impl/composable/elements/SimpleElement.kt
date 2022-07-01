package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.settings.impl.R

@Composable
fun SimpleElement(
    modifier: Modifier = Modifier,
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    onClick: (() -> Unit)? = null,
    titleTextStyle: TextStyle = LocalTypography.current.bodyR14.copy(
        color = LocalPallet.current.text100
    )
) {
    val title: String? = titleId?.let { stringResource(id = it) }
    val description = descriptionId?.let { stringResource(id = it) }

    var rowModifier = modifier
        .heightIn(min = 48.dp)
        .padding(all = 12.dp)
        .fillMaxWidth()
    if (onClick != null) {
        rowModifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
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

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun SimpleElementPreview() {
    SimpleElement(
        modifier = Modifier,
        titleId = R.string.experimental_screen_streaming,
        descriptionId = R.string.experimental_screen_streaming_desc
    )
}
