package com.flipperdevices.filemanager.download.impl.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.ui.ktx.elements.FlipperProgressIndicator
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.filemanager.download.impl.viewmodel.DownloadViewModel
import flipperapp.components.filemngr.download.impl.generated.resources.fm_downloading_file
import flipperapp.components.filemngr.download.impl.generated.resources.fm_in_progress_file_size
import flipperapp.components.filemngr.download.impl.generated.resources.fm_in_progress_speed
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.download.impl.generated.resources.Res as FDR

@Composable
private fun InProgressDetailComposable(
    state: DownloadViewModel.State.Downloading,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(
            FDR.string.fm_downloading_file,
            state.fullPath.name,
            state.downloadedSize.toFormattedSize(),
            state.totalSize.toFormattedSize()
        ),
        style = LocalTypography.current.subtitleM12,
        color = LocalPalletV2.current.text.body.secondary,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun InProgressTitleComposable(
    state: DownloadViewModel.State.Downloading,
    modifier: Modifier = Modifier
) {
    Text(
        text = state.fullPath.name,
        style = LocalTypography.current.titleB18,
        color = LocalPalletV2.current.text.title.primary,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun InProgressComposable(
    state: DownloadViewModel.State.Downloading,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (state.totalSize == 0L) 0f else state.downloadedSize / state.totalSize.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "Progress"
    )
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InProgressTitleComposable(state)
        Spacer(Modifier.height(12.dp))
        FlipperProgressIndicator(
            modifier = Modifier.padding(horizontal = 32.dp),
            accentColor = LocalPalletV2.current.action.blue.border.primary.default,
            secondColor = LocalPallet.current.actionOnFlipperProgress,
            painter = null,
            percent = animatedProgress
        )
        Spacer(Modifier.height(8.dp))
        InProgressDetailComposable(state)
        Text(
            text = stringResource(
                FDR.string.fm_in_progress_file_size,
                state.downloadedSize.toFormattedSize(),
                state.totalSize.toFormattedSize()
            ),
            style = LocalTypography.current.subtitleM12,
            color = LocalPalletV2.current.text.body.secondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(
                FDR.string.fm_in_progress_speed,
                state.downloadSpeed.toFormattedSize(),
            ),
            style = LocalTypography.current.subtitleM12,
            color = LocalPalletV2.current.text.body.secondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
