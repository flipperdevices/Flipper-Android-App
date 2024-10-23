package com.flipperdevices.newfilemanager.impl.composable.dialog

import android.text.format.Formatter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.newfilemanager.impl.model.DownloadProgress
import com.flipperdevices.newfilemanager.impl.model.ShareState
import com.flipperdevices.newfilemanager.impl.model.SpeedState
import flipperapp.components.newfilemanager.impl.generated.resources.Res
import flipperapp.components.newfilemanager.impl.generated.resources.filemanager_error
import flipperapp.components.newfilemanager.impl.generated.resources.filemanager_error_title
import flipperapp.components.newfilemanager.impl.generated.resources.share_dialog_btn_close
import flipperapp.components.newfilemanager.impl.generated.resources.share_dialog_progress_infinite_text
import flipperapp.components.newfilemanager.impl.generated.resources.share_dialog_progress_text
import flipperapp.components.newfilemanager.impl.generated.resources.share_dialog_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ComposableProgressDialog(
    shareState: ShareState,
    speedState: SpeedState,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            when (shareState) {
                ShareState.Error -> stringResource(Res.string.filemanager_error_title)
                is ShareState.Ready -> Text(
                    text = stringResource(
                        Res.string.share_dialog_title,
                        shareState.name
                    )
                )
            }
        },
        buttons = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(
                    modifier = Modifier.padding(all = 16.dp),
                    onClick = onCancel
                ) {
                    Text(text = stringResource(Res.string.share_dialog_btn_close))
                }
            }
        },
        text = {
            when (shareState) {
                ShareState.Error -> Text(stringResource(Res.string.filemanager_error))
                is ShareState.Ready -> when (shareState.downloadProgress) {
                    is DownloadProgress.Fixed -> ComposableFixedProgress(
                        shareState.downloadProgress,
                        speedState
                    )

                    is DownloadProgress.Infinite -> ComposableInfiniteProgress(
                        shareState.downloadProgress,
                        speedState
                    )
                }
            }
        }
    )
}

@Composable
fun ComposableFixedProgress(
    fixedProgress: DownloadProgress.Fixed,
    speedState: SpeedState,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = fixedProgress.toProgressFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    val downloadedSize = Formatter.formatFileSize(
        LocalContext.current,
        fixedProgress.progress
    )
    val totalSize = Formatter.formatFileSize(
        LocalContext.current,
        fixedProgress.totalSize
    )

    Column(modifier) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(
                Res.string.share_dialog_progress_text,
                downloadedSize,
                totalSize,
                getSpeedText(speedState)
            )
        )
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = animatedProgress,
            color = LocalPallet.current.accent
        )
    }
}

@Composable
private fun getSpeedText(speedState: SpeedState): String {
    return when (speedState) {
        is SpeedState.Ready ->
            "${
                Formatter.formatFileSize(
                    LocalContext.current,
                    speedState.receiveBytesInSec
                )
            }/s download/${
                Formatter.formatFileSize(
                    LocalContext.current,
                    speedState.transmitBytesInSec
                )
            }/s upload"

        SpeedState.Unknown -> "Unknown"
    }
}

@Composable
fun ComposableInfiniteProgress(
    infiniteProgress: DownloadProgress.Infinite,
    speedState: SpeedState,
    modifier: Modifier = Modifier,
) {
    val downloadedSize = Formatter.formatFileSize(
        LocalContext.current,
        infiniteProgress.progress
    )
    Column(modifier) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(
                Res.string.share_dialog_progress_infinite_text,
                downloadedSize,
                getSpeedText(speedState)
            )
        )
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }
}
