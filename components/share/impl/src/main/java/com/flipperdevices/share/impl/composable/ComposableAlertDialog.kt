package com.flipperdevices.share.impl.composable

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.share.impl.R
import com.flipperdevices.share.impl.model.DownloadProgress
import com.flipperdevices.share.model.ShareFile

@Composable
fun ComposableAlertDialog(
    shareFile: ShareFile,
    downloadProgress: DownloadProgress,
    onCancel: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = downloadProgress.toProgressFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = stringResource(R.string.share_dialog_title, shareFile.name))
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
                    Text(text = stringResource(R.string.share_dialog_btn_close))
                }
            }
        },
        text = {
            val downloadedSize = Formatter.formatFileSize(
                LocalContext.current,
                downloadProgress.progress
            )
            val totalSize = Formatter.formatFileSize(
                LocalContext.current,
                downloadProgress.totalSize
            )
            Column() {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = stringResource(
                        R.string.share_dialog_progress_text,
                        downloadedSize, totalSize
                    )
                )
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = animatedProgress
                )
            }
        }
    )
}
