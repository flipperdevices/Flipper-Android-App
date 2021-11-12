package com.flipperdevices.share.export.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.api.ShareApi
import com.flipperdevices.share.export.composable.ComposableShare
import com.flipperdevices.share.model.ShareFile
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ShareApiImpl @Inject constructor() : ShareApi {
    @Composable
    override fun AlertDialogDownload(shareFile: ShareFile, onCancel: () -> Unit) {
        ComposableShare(shareFile, onCancel)
    }
}
