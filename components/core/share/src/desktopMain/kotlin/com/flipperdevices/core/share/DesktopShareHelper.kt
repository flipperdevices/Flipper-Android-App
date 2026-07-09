package com.flipperdevices.core.share

import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<PlatformShareHelper>())
class DesktopShareHelper @Inject constructor() : PlatformShareHelper {

    override fun provideSharableFile(fileName: String): PlatformSharableFile {
        error("The desktop feature is not yet implemented!")
    }

    override fun shareFile(file: PlatformSharableFile, title: String) {
        error("The desktop feature is not yet implemented!")
    }
}
