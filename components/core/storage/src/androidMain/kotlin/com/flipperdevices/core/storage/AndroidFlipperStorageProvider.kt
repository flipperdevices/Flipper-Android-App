package com.flipperdevices.core.storage

import android.content.Context
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesBinding
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<FlipperStorageProvider>())
class AndroidFlipperStorageProvider @Inject constructor(
    context: Context
) : FlipperStorageProvider() {
    override val fileSystem = FileSystem.SYSTEM
    override val tmpPath = context.cacheDir.toOkioPath()
    override val rootPath = context.filesDir.toOkioPath()
}
