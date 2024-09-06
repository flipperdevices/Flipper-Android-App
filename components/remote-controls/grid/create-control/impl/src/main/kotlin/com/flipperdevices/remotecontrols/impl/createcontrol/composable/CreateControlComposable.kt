package com.flipperdevices.remotecontrols.impl.createcontrol.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.remotecontrols.grid.createcontrol.impl.R
import com.flipperdevices.remotecontrols.impl.createcontrol.viewmodel.SaveRemoteControlViewModel.State

@Composable
internal fun CreateControlComposable(state: State) {
    AnimatedContent(
        targetState = state,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        contentKey = {
            when (it) {
                State.CouldNotModifyFiles -> 0
                is State.Finished -> 1
                State.InProgress.ModifyingFiles -> 2
                is State.InProgress.Synchronizing -> 3
                State.KeyNotFound -> 4
                State.Pending -> 5
            }
        },
        content = { animatedState ->
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleComposable(animatedState)
                Spacer(Modifier.height(24.dp))
                ProgressIndicatorComposable((animatedState as? State.InProgress.Synchronizing)?.progress)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.configuring_desc),
                    color = LocalPalletV2.current.text.body.secondary,
                    style = LocalTypography.current.subtitleM12,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}

@Composable
private fun TitleComposable(
    state: State,
    modifier: Modifier = Modifier
) {
    Text(
        text = when (state) {
            State.InProgress.ModifyingFiles -> stringResource(R.string.configuring_files_title)
            is State.InProgress.Synchronizing -> {
                val progressAnimated by animateFloatAsState(state.progress)
                LocalContext.current.getString(
                    R.string.archive_sync_percent,
                    progressAnimated.roundPercentToString()
                )
            }

            else -> stringResource(R.string.configuring_title)
        },
        color = LocalPalletV2.current.text.body.primary,
        style = LocalTypography.current.titleB18,
        modifier = modifier
    )
}

@Composable
private fun ProgressIndicatorComposable(
    progress: Float?,
    modifier: Modifier = Modifier
) {
    when {
        progress != null -> {
            val progressAnimated by animateFloatAsState(progress)
            CircularProgressIndicator(
                modifier = modifier.size(48.dp),
                color = LocalPallet.current.accentSecond,
                progress = progressAnimated
            )
        }

        else -> {
            CircularProgressIndicator(
                modifier = modifier.size(48.dp),
                color = LocalPallet.current.accentSecond,
            )
        }
    }
}

@Preview
@Composable
private fun CreateControlComposablePendingPreview() {
    FlipperThemeInternal {
        CreateControlComposable(State.Pending)
    }
}

@Preview
@Composable
private fun CreateControlComposableModifyingPreview() {
    FlipperThemeInternal {
        CreateControlComposable(State.InProgress.ModifyingFiles)
    }
}

@Preview
@Composable
private fun CreateControlComposableSyncingPreview() {
    FlipperThemeInternal {
        CreateControlComposable(State.InProgress.Synchronizing(progress = 0.3f))
    }
}
