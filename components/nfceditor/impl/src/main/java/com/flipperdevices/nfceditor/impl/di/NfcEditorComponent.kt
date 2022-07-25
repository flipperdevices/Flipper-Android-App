package com.flipperdevices.nfceditor.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface NfcEditorComponent {
    fun inject(viewModel: NfcEditorViewModel)
}
