package com.flipperdevices.firstpair.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.firstpair.impl.fragments.FirstPairFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class FirstPairApiImpl @Inject constructor() : FirstPairApi {
    override fun getFirstPairScreen(): Screen {
        return FragmentScreen { FirstPairFragment() }
    }
}
