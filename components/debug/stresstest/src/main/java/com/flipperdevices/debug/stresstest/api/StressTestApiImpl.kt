package com.flipperdevices.debug.stresstest.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.debug.api.StressTestApi
import com.flipperdevices.debug.stresstest.fragments.StressTestFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class StressTestApiImpl @Inject constructor() : StressTestApi {
    override fun getStressTestScreen(): Screen {
        return FragmentScreen { StressTestFragment() }
    }
}
