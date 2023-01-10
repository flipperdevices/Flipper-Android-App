package com.flipperdevices.faphub.fapscreen.impl.composable.description

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.markdown.annotatedStringFromMarkdown
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.fapscreen.impl.R

private const val MAX_CHANGELOG_LINE = 4
private val DEFAULT_CHANGELOG
    get() = String((Array(size = 4) { '\n' }).toCharArray())

@Composable
fun ColumnScope.ComposableFapChangelogText(
    changelog: String?
) {
    var showMoreButton by remember { mutableStateOf(false) }
    var maxChangelogLines by remember { mutableStateOf(MAX_CHANGELOG_LINE) }
    Text(
        modifier = Modifier.padding(bottom = 8.dp, top = 24.dp),
        text = stringResource(R.string.fapscreen_changelog_title),
        style = LocalTypography.current.buttonM16,
        color = LocalPallet.current.text100
    )

    Text(
        modifier = if (changelog == null) {
            Modifier
                .fillMaxWidth()
                .placeholderConnecting()
        } else {
            Modifier
        },
        text = changelog?.let { annotatedStringFromMarkdown(it) }
            ?: AnnotatedString(DEFAULT_CHANGELOG),
        style = LocalTypography.current.bodyR14,
        color = LocalPallet.current.text100,
        maxLines = maxChangelogLines,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = {
            if (it.hasVisualOverflow) {
                showMoreButton = changelog != null
            } else {
                showMoreButton = false
            }
        }
    )

    if (showMoreButton) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    maxChangelogLines = Int.MAX_VALUE
                    showMoreButton = false
                }
                .padding(top = 2.dp),
            text = stringResource(R.string.fapscreen_changelog_more),
            textAlign = TextAlign.End,
            style = LocalTypography.current.subtitleM12,
            color = LocalPallet.current.text30
        )
    }
}
