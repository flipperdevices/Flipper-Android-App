package com.flipperdevices.connection.impl.di

import com.flipperdevices.connection.impl.viewmodel.ConnectionStatusViewModel
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface ConnectionComponent {
    fun inject(viewModel: ConnectionStatusViewModel)
}
