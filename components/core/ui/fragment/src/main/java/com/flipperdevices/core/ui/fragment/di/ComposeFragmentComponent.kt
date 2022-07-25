package com.flipperdevices.core.ui.fragment.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.keyinputbus.KeyInputBus
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider

@ContributesTo(AppGraph::class)
interface ComposeFragmentComponent {
    val keyInputBusProvider: Provider<KeyInputBus>
}
