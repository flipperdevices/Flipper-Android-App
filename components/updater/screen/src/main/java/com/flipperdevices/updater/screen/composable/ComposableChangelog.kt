package com.flipperdevices.updater.screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.markdown.ComposableMarkdown
import com.flipperdevices.core.ui.scrollbar.ComposableColumnScrollbarWithIndicator
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.updater.screen.R

@Composable
fun ComposableChangelog(
    changelog: String,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        Divider(
            Modifier
                .height(1.dp)
                .background(LocalPallet.current.divider12)
        )
        ComposableColumnScrollbarWithIndicator(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 3.dp)
        ) {
            Text(
                text = stringResource(id = R.string.update_screen_whats_new),
                style = LocalTypography.current.titleSB18,
                textAlign = TextAlign.Left
            )
            ComposableMarkdown(
                content = changelog,
                modifier = Modifier.fillMaxSize().padding(4.dp)
            )
        }
        Divider(
            Modifier
                .height(1.dp)
                .background(LocalPallet.current.divider12)
        )
    }
}
