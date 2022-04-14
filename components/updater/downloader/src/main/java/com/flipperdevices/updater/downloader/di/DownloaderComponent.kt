package com.flipperdevices.updater.downloader.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.downloader.api.DownloaderApiImpl
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface DownloaderComponent {
    fun inject(downloaderApiImpl: DownloaderApiImpl)
}
