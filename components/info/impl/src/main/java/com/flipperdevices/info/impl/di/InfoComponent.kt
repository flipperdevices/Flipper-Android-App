package com.flipperdevices.info.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface InfoComponent {
    fun inject(viewModel: FirmwareUpdateViewModel)
}
