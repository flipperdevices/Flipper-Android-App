package com.flipperdevices.settings.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.settings.impl.fragments.SettingsFragment
import com.flipperdevices.settings.impl.viewmodels.DebugViewModel
import com.flipperdevices.settings.impl.viewmodels.SettingsViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface SettingsComponent {
    fun inject(viewModel: SettingsViewModel)
    fun inject(viewModel: DebugViewModel)
    fun inject(settingsFragment: SettingsFragment)
}
