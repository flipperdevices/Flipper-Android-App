package com.flipperdevices.filemanager.receive.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.receive.viewmodel.ReceiveViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface ShareReceiveComponent {
    fun inject(viewModel: ReceiveViewModel)
}
