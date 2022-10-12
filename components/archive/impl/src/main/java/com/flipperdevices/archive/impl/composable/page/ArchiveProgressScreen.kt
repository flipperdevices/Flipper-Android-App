package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.impl.R
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ArchiveProgressScreen(
    inProgressState: SynchronizationState.InProgress,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = LocalPallet.current.accentSecond,
            strokeWidth = 2.dp
        )
        Text(
            modifier = Modifier.padding(top = 14.dp),
            text = LocalContext.current.getString(
                R.string.archive_sync_percent,
                inProgressState.progress.roundPercentToString()
            ),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text60,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .padding(top = 14.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(),
                    onClick = onCancel
                ),
            text = stringResource(R.string.archive_sync_cancel),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.accentSecond
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableArchiveProgressScreenPreview() {
    FlipperThemeInternal {
        ArchiveProgressScreen(SynchronizationState.InProgress(0f)) { }
    }
}
