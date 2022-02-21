package com.flipperdevices.deeplink.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.impl.parser.delegates.DeepLinkFlipperFormatSharing
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface DeepLinkComponent {
    fun inject(delegate: DeepLinkFlipperFormatSharing)
}
