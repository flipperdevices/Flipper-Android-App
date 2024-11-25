package com.flipperdevices.filemanager.upload.impl.composable

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
import com.flipperdevices.filemanager.upload.api.UploaderDecomposeComponent
import flipperapp.components.filemngr.upload.impl.generated.resources.fm_in_progress_file_size
import flipperapp.components.filemngr.upload.impl.generated.resources.fm_in_progress_items
import flipperapp.components.filemngr.upload.impl.generated.resources.fm_in_progress_speed
import flipperapp.components.filemngr.upload.impl.generated.resources.fm_uploading_file
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.upload.impl.generated.resources.Res as FUR

@Composable
private fun InProgressDetailComposable(
    state: UploaderDecomposeComponent.State.Uploading,
    modifier: Modifier = Modifier
) {
    if (state.totalItemsAmount > 1) {
        Text(
            text = stringResource(
                FUR.string.fm_uploading_file,
                state.currentItem.fileName,
                state.currentItem.uploadedSize.toFormattedSize(),
                state.currentItem.totalSize.toFormattedSize()
            ),
            style = LocalTypography.current.subtitleM12,
            color = LocalPalletV2.current.text.body.secondary,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InProgressTitleComposable(
    state: UploaderDecomposeComponent.State.Uploading,
    modifier: Modifier = Modifier
) {
    Text(
        text = when {
            state.totalItemsAmount == 1 -> state.currentItem.fileName
            else -> stringResource(
                FUR.string.fm_in_progress_items,
                state.currentItemIndex.plus(1),
                state.totalItemsAmount
            )
        },
        style = LocalTypography.current.titleB18,
        color = LocalPalletV2.current.text.title.primary,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun InProgressComposable(
    state: UploaderDecomposeComponent.State.Uploading,
    speed: Long?,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (state.totalSize == 0L) 0f else state.uploadedSize / state.totalSize.toFloat(),
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
                FUR.string.fm_in_progress_file_size,
                state.uploadedSize.toFormattedSize(),
                state.totalSize.toFormattedSize()
            ),
            style = LocalTypography.current.subtitleM12,
            color = LocalPalletV2.current.text.body.secondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        speed?.let {
            Text(
                text = stringResource(
                    FUR.string.fm_in_progress_speed,
                    speed.toFormattedSize(),
                ),
                style = LocalTypography.current.subtitleM12,
                color = LocalPalletV2.current.text.body.secondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
