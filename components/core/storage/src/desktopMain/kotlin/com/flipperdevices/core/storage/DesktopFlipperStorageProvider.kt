package com.flipperdevices.core.storage

import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesBinding
import okio.FileSystem
import okio.Path.Companion.toPath
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<FlipperStorageProvider>())
class DesktopFlipperStorageProvider @Inject constructor() : FlipperStorageProvider() {
    override val fileSystem = FileSystem.SYSTEM
    override val tmpPath = System.getProperty("java.io.tmpdir").toPath()
    override val rootPath = System.getProperty("user.home").toPath().resolve(".flipper")
}
