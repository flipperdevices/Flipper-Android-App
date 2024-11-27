package com.flipperdevices.core.share

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import okio.Path.Companion.toOkioPath
import javax.inject.Inject

@ContributesBinding(AppGraph::class, PlatformShareHelper::class)
class AndroidShareHelper @Inject constructor(
    private val context: Context
) : PlatformShareHelper {

    override fun provideSharableFile(fileName: String): PlatformSharableFile {
        val path = SharableFile(context, fileName).toOkioPath()
        return PlatformSharableFile(path)
    }

    override fun shareFile(file: PlatformSharableFile, title: String) {
        ShareHelper.shareFile(
            context = context,
            file = file,
            text = title
        )
    }
}
