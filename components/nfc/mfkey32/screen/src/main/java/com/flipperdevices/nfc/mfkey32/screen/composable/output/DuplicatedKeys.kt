package com.flipperdevices.nfc.mfkey32.screen.composable.output

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfc.mfkey32.screen.R
import com.flipperdevices.nfc.mfkey32.screen.model.DuplicatedSource

@Suppress("FunctionNaming")
fun LazyListScope.DuplicatedKeys(keys: Map<String, DuplicatedSource>) {
    item {
        Text(
            modifier = Modifier.padding(top = 24.dp, start = 14.dp, end = 14.dp, bottom = 10.dp),
            text = stringResource(R.string.mfkey32_founded_duplicated_title, keys.size),
            style = LocalTypography.current.buttonM16,
            color = LocalPallet.current.text100
        )
    }
    items(keys.keys.toList()) { key ->
        val textStyle = LocalTypography.current.subtitleR12
        val sourceTextId = when (keys[key]) {
            DuplicatedSource.FLIPPER -> R.string.mfkey32_founded_duplicated_flipper
            DuplicatedSource.USER -> R.string.mfkey32_founded_duplicated_user
            null -> null
        }
        val sourceText = sourceTextId?.let { stringResource(id = it) }
        val text = if (sourceText != null) {
            buildAnnotatedString {
                append(key)
                append(" ")
                withStyle(
                    textStyle.toSpanStyle().copy(
                        color = LocalPallet.current.text40
                    )
                ) {
                    append(" (")
                    append(sourceText)
                    append(")")
                }
            }
        } else AnnotatedString(key)
        SelectionContainer {
            Text(
                modifier = Modifier.padding(
                    start = 14.dp, end = 14.dp, top = 4.dp, bottom = 4.dp
                ),
                text = text,
                style = textStyle
            )
        }
    }
}
