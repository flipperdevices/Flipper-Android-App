package com.flipperdevices.core.storage

import android.content.Context
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperStorageProvider::class)
class AndroidFlipperStorageProvider @Inject constructor(
    context: Context
) : FlipperStorageProvider() {
    override val fileSystem = FileSystem.SYSTEM
    override val tmpPath = context.cacheDir.toOkioPath()
    override val rootPath = context.filesDir.toOkioPath()
}
