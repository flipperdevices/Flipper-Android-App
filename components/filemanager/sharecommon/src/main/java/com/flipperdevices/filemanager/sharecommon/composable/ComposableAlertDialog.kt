package com.flipperdevices.filemanager.sharecommon.composable

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
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.filemanager.sharecommon.R
import com.flipperdevices.filemanager.sharecommon.model.DownloadProgress

@Composable
fun ComposableAlertDialog(
    title: String,
    downloadProgress: DownloadProgress,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = title)
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
            when (downloadProgress) {
                is DownloadProgress.Fixed -> {
                    ComposableFixedProgress(downloadProgress)
                }
                is DownloadProgress.Infinite -> {
                    ComposableInfiniteProgress(downloadProgress)
                }
            }
        }
    )
}

@Composable
fun ComposableFixedProgress(fixedProgress: DownloadProgress.Fixed) {
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
    val speed = Formatter.formatFileSize(
        LocalContext.current,
        fixedProgress.speedBytesInSecond
    )
    Column {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(
                R.string.share_dialog_progress_text,
                downloadedSize,
                totalSize,
                "$speed/s"
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
fun ComposableInfiniteProgress(infiniteProgress: DownloadProgress.Infinite) {
    val downloadedSize = Formatter.formatFileSize(
        LocalContext.current,
        infiniteProgress.progress
    )
    val speed = Formatter.formatFileSize(
        LocalContext.current,
        infiniteProgress.speedBytesInSecond
    )
    Column {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(
                R.string.share_dialog_progress_infinite_text,
                downloadedSize,
                "$speed/s"
            )
        )
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }
}
