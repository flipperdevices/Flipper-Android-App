package com.flipperdevices.updater.card.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.card.viewmodel.FlipperStateViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateCardViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateRequestViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface CardComponent {
    fun inject(viewModel: FlipperStateViewModel)
    fun inject(viewModel: UpdateCardViewModel)
    fun inject(viewModel: UpdateRequestViewModel)
}
