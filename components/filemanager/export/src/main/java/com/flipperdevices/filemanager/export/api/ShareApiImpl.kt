package com.flipperdevices.filemanager.export.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.api.share.ShareApi
import com.flipperdevices.filemanager.api.share.ShareFile
import com.flipperdevices.filemanager.export.composable.ComposableShare
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ShareApiImpl @Inject constructor() : ShareApi {
    @Composable
    override fun AlertDialogDownload(shareFile: ShareFile, onCancel: () -> Unit) {
        ComposableShare(shareFile, onCancel)
    }
}
