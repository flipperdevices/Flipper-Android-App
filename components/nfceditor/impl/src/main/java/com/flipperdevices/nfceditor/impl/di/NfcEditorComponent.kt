package com.flipperdevices.nfceditor.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.keyinputbus.KeyInputBus
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider

@ContributesTo(AppGraph::class)
interface NfcEditorComponent {
    val keyInputBusProvider: Provider<KeyInputBus>

    fun inject(viewModel: NfcEditorViewModel)
}
