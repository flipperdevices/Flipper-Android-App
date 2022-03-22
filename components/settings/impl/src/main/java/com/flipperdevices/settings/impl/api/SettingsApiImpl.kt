package com.flipperdevices.settings.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.settings.api.SettingsApi
import com.flipperdevices.settings.impl.fragments.SettingsFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class SettingsApiImpl @Inject constructor() : SettingsApi {
    override fun getSettingsScreen(): Screen {
        return FragmentScreen { SettingsFragment() }
    }
}
