package com.flipperdevices.debug.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.debug.api.DebugScreenApi
import com.flipperdevices.debug.impl.fragments.DebugScreenFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class DebugScreenImpl @Inject constructor() : DebugScreenApi {
    override fun getDebugScreen(): Screen {
        return FragmentScreen { DebugScreenFragment() }
    }
}
