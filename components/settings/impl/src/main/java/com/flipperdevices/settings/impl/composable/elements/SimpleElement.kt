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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.settings.impl.R

@Composable
fun SimpleElement(
    modifier: Modifier = Modifier,
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    onClick: (() -> Unit)? = null,
    titleTextStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp
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
                    fontSize = 12.sp,
                    color = colorResource(DesignSystem.color.black_40)
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
    SimpleElement(Modifier, R.string.experimental_screen_streaming, R.string.experimental_screen_streaming_desc)
}
