package com.flipperdevices.share.receive.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.receive.viewmodels.KeyReceiveViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface KeyReceiveComponent {
    fun inject(viewModel: KeyReceiveViewModel)
}
