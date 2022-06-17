package com.flipperdevices.updater.card.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.card.viewmodel.DeviceConnectedViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateCardViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateRequestViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateStateViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface CardComponent {
    fun inject(viewModel: UpdateStateViewModel)
    fun inject(viewModel: UpdateCardViewModel)
    fun inject(viewModel: UpdateRequestViewModel)
    fun inject(viewModel: DeviceConnectedViewModel)
}
