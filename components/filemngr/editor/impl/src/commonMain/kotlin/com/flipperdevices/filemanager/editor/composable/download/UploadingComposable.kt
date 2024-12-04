package com.flipperdevices.filemanager.editor.composable.download

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.filemanager.ui.components.transfer.FileTransferFullScreenComposable
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_cancel
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_status_speed
import okio.Path
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.editor.impl.generated.resources.Res as FME

@Composable
fun UploadingComposable(
    title: String,
    progress: Float,
    fullPathOnFlipper: Path,
    current: Long,
    max: Long,
    speed: Long,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    FileTransferFullScreenComposable(
        modifier = modifier,
        title = title,
        actionText = stringResource(FME.string.fme_cancel),
        onActionClick = onCancel,
        progressText = fullPathOnFlipper.name,
        progress = progress,
        progressDetailText = if (max == 0L) null else "${current.toFormattedSize()}/${max.toFormattedSize()}",
        progressTitle = fullPathOnFlipper.name,
        speedText = when (speed) {
            0L -> null
            else -> stringResource(
                resource = FME.string.fme_status_speed,
                speed.toFormattedSize()
            )
        }
    )
}
