package com.flipperdevices.core.share

import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, PlatformShareHelper::class)
class DesktopShareHelper @Inject constructor() : PlatformShareHelper {

    override fun provideSharableFile(fileName: String): PlatformSharableFile {
        error("The desktop feature is not yet implemented!")
    }

    override fun shareFile(file: PlatformSharableFile, title: String) {
        error("The desktop feature is not yet implemented!")
    }
}
