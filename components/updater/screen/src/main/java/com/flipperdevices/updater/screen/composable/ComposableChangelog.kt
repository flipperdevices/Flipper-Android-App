package com.flipperdevices.updater.screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.markdown.ComposableMarkdown
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.updater.screen.R

@Composable
fun ComposableChangelog(
    changelog: String
) {
    Column(Modifier.fillMaxSize()) {
        Divider(Modifier.height(1.dp).background(LocalPallet.current.divider12))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp, bottom = 3.dp)
        ) {
            Text(
                text = stringResource(id = R.string.update_screen_whats_new),
                style = LocalTypography.current.titleSB18,
                textAlign = TextAlign.Left
            )
            ComposableMarkdown(
                content = removeLineBreakChangelog(changelog),
                modifier = Modifier.fillMaxSize()
            )
        }
        Divider(Modifier.height(1.dp).background(LocalPallet.current.divider12))
    }
}

private fun removeLineBreakChangelog(changelog: String): String {
    return changelog
        .replace("\r\n\r\n", "\r\n")
        .replace("\r", "")
}
