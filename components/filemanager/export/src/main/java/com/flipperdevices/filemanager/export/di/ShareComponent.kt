package com.flipperdevices.filemanager.export.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.export.viewmodel.ShareViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface ShareComponent {
    fun inject(viewModel: ShareViewModel)
}
