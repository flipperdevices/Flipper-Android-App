package com.flipperdevices.archive.impl.composable.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.impl.R
import com.flipperdevices.bridge.api.model.FlipperSerialSpeed
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
private fun AnimatedProgressComposable(progress: Float) {
    Box(
        modifier = Modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        val progressAnimated by animateFloatAsState(targetValue = progress)
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            color = LocalPalletV2.current.action.blue.background.primary.default,
            backgroundColor = LocalPalletV2.current.action.neutral.background.tertiary.default,
            strokeWidth = 2.dp,
            progress = progressAnimated
        )
        Text(
            modifier = Modifier,
            text = progressAnimated.roundPercentToString(),
            style = LocalTypography.current.bodyM14,
            color = LocalPalletV2.current.text.body.secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AnimatedStatusComposable(inProgressState: SynchronizationState.InProgress) {
    AnimatedContent(
        targetState = inProgressState,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        contentKey = {
            when (it) {
                is SynchronizationState.InProgress.Default -> 0
                is SynchronizationState.InProgress.Favorites -> 1
                is SynchronizationState.InProgress.Prepare -> 2
                is SynchronizationState.InProgress.PrepareHashes -> it.keyType.name
                is SynchronizationState.InProgress.FileInProgress -> it.fileName
            }
        },
    ) { animatedInProgressState ->
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = when (animatedInProgressState) {
                is SynchronizationState.InProgress.FileInProgress -> {
                    LocalContext.current.getString(
                        R.string.archive_updating,
                        animatedInProgressState.fileName
                    )
                }

                is SynchronizationState.InProgress.Default -> {
                    LocalContext.current.getString(R.string.archive_sync_progress)
                }

                is SynchronizationState.InProgress.Favorites -> {
                    LocalContext.current.getString(R.string.archive_favorites)
                }

                is SynchronizationState.InProgress.Prepare -> {
                    LocalContext.current.getString(R.string.archive_preparing)
                }

                is SynchronizationState.InProgress.PrepareHashes -> {
                    LocalContext.current.getString(
                        R.string.archive_hashes,
                        animatedInProgressState.keyType.humanReadableName
                    )
                }
            },
            style = LocalTypography.current.bodyM14,
            color = LocalPalletV2.current.text.body.secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ArchiveProgressScreen(
    inProgressState: SynchronizationState.InProgress,
    onCancel: () -> Unit,
    speed: FlipperSerialSpeed?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LocalPallet.current.background)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier,
            text = LocalContext.current.getString(
                R.string.archive_syncing,
            ),
            style = LocalTypography.current.titleB18,
            color = LocalPallet.current.text60,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        AnimatedProgressComposable(inProgressState.progress)
        Spacer(Modifier.height(8.dp))
        AnimatedStatusComposable(inProgressState)
        Spacer(Modifier.height(8.dp))
        Text(
            modifier = Modifier,
            text = when (speed) {
                null -> ""
                else -> LocalContext.current.getString(
                    R.string.archive_speed,
                    speed.receiveBytesInSec.toFormattedSize()
                )
            },
            style = LocalTypography.current.bodyM14,
            color = LocalPalletV2.current.text.body.secondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier
                .clickableRipple(onClick = onCancel),
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
        ArchiveProgressScreen(
            inProgressState = SynchronizationState.InProgress.Default(
                progress = 0.3f,
            ),
            speed = FlipperSerialSpeed(
                receiveBytesInSec = 10L,
                transmitBytesInSec = 10L
            ),
            onCancel = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableArchiveProgressFileScreenPreview() {
    FlipperThemeInternal {
        ArchiveProgressScreen(
            inProgressState = SynchronizationState.InProgress.FileInProgress(
                progress = 0.3f,
                fileName = "file.ir"
            ),
            speed = FlipperSerialSpeed(
                receiveBytesInSec = 10L,
                transmitBytesInSec = 10L
            ),
            onCancel = {}
        )
    }
}
