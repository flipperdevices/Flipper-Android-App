package com.flipperdevices.share.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.impl.viewmodel.ShareViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface ShareComponent {
    fun inject(viewModel: ShareViewModel)
}
