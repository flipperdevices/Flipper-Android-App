package com.flipperdevices.core.storage

import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import okio.FileSystem
import okio.Path.Companion.toPath
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperStorageProvider::class)
class DesktopFlipperStorageProvider @Inject constructor() : FlipperStorageProvider() {
    override val fileSystem = FileSystem.SYSTEM
    override val tmpPath = System.getProperty("java.io.tmpdir").toPath()
    override val rootPath = System.getProperty("user.home").toPath().resolve(".flipper")
}
