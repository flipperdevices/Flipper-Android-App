package com.flipperdevices.share.receive.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.receive.ReferrerReceiver
import com.flipperdevices.share.receive.fragments.KeyReceiveFragment
import com.flipperdevices.share.receive.viewmodels.KeyReceiveViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface KeyReceiveComponent {
    fun inject(fragment: KeyReceiveFragment)
    fun inject(viewModel: KeyReceiveViewModel)
    fun inject(receiver: ReferrerReceiver)
}
