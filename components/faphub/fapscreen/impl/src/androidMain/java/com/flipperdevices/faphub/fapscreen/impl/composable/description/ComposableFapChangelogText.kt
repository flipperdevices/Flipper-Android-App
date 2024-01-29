package com.flipperdevices.faphub.fapscreen.impl.composable.description

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.markdown.ComposableMarkdown
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.fapscreen.impl.R

private const val MAX_CHANGELOG_LINE = 4
private val DEFAULT_CHANGELOG
    get() = String((Array(size = 4) { '\n' }).toCharArray())

@Composable
fun ColumnScope.ComposableFapChangelogText(
    changelog: String?,
    modifier: Modifier = Modifier
) {
    var maxChangelogLines by remember { mutableStateOf(MAX_CHANGELOG_LINE) }
    Text(
        modifier = modifier.padding(bottom = 8.dp, top = 32.dp),
        text = stringResource(R.string.fapscreen_changelog_title),
        style = LocalTypography.current.titleB18,
        color = LocalPallet.current.text100
    )

    val (processedChangelog, hasOverflow) = remember(changelog, maxChangelogLines) {
        if (changelog == null) {
            return@remember null to false
        }
        val lines = changelog.lines()
        if (lines.size > maxChangelogLines) {
            return@remember lines.take(maxChangelogLines).joinToString("\n") to true
        }
        return@remember changelog to false
    }

    SelectionContainer {
        ComposableMarkdown(
            modifier = if (processedChangelog == null) {
                Modifier
                    .fillMaxWidth()
                    .placeholderConnecting()
            } else {
                Modifier.fillMaxWidth()
            },
            content = processedChangelog ?: DEFAULT_CHANGELOG,
        )
    }

    if (hasOverflow) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    maxChangelogLines = Int.MAX_VALUE
                }
                .padding(top = 2.dp),
            text = stringResource(R.string.fapscreen_changelog_more),
            textAlign = TextAlign.End,
            style = LocalTypography.current.subtitleM12,
            color = LocalPallet.current.text30
        )
    }
}
