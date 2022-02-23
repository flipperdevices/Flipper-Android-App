package com.flipperdevices.filemanager.export.api

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.filemanager.api.share.ShareApi
import com.flipperdevices.filemanager.api.share.ShareFile
import com.flipperdevices.filemanager.export.BuildConfig
import com.flipperdevices.filemanager.export.composable.ComposableShare
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ContributesBinding(AppGraph::class)
class ShareApiImpl @Inject constructor() : ShareApi {
    @Composable
    override fun AlertDialogDownload(shareFile: ShareFile, onCancel: () -> Unit) {
        ComposableShare(shareFile, onCancel)
    }

    override suspend fun getExternalUriForFile(
        context: Context,
        temporaryFile: File,
        displayName: String?
    ) = withContext(Dispatchers.IO) {
        val shareFile = File(
            FlipperStorageProvider.getSharedKeyFolder(context),
            temporaryFile.name
        )
        temporaryFile.copyTo(shareFile, overwrite = true)

        return@withContext if (displayName == null) {
            FileProvider.getUriForFile(
                context,
                BuildConfig.SHARE_FILE_AUTHORITIES,
                shareFile
            )
        } else FileProvider.getUriForFile(
            context,
            BuildConfig.SHARE_FILE_AUTHORITIES,
            shareFile,
            displayName
        )
    }
}
