package com.flipperdevices.firstpair.impl.composable.help

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.markdown.ClickableUrlText
import com.flipperdevices.core.ui.R as DesignSystem

@Composable
fun ComposableHelpItem(index: Int, data: HelpOptions) {
    Row {
        val titleStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.W400,
            color = colorResource(DesignSystem.color.black_100)
        )

        Text(
            modifier = Modifier.padding(end = 2.dp),
            text = "${index + 1}.",
            style = titleStyle
        )
        Column() {
            Text(
                text = stringResource(data.title),
                style = titleStyle
            )
            ComposableHelpOptionsDescription(
                data = data
            )
        }
    }
}

@Composable
fun ComposableHelpOptionsDescription(
    modifier: Modifier = Modifier,
    data: HelpOptions
) {
    val descriptionStyle = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        color = colorResource(DesignSystem.color.accent_secondary),
        textDecoration = TextDecoration.Underline
    )

    if (data.description == null) {
        Text(
            modifier = modifier,
            text = "",
            style = descriptionStyle
        )
        return
    }

    if (data is HelpOptions.CustomOpenLinkHandler) {
        val context = LocalContext.current

        Text(
            modifier = modifier.clickable {
                data.onClick(context)
            },
            text = stringResource(data.description),
            style = descriptionStyle
        )
        return
    }

    ClickableUrlText(
        modifier = modifier,
        markdownResId = data.description,
        style = descriptionStyle
    )
}
