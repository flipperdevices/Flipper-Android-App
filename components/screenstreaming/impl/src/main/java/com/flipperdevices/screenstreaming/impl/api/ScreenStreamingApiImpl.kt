package com.flipperdevices.screenstreaming.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.screenstreaming.api.ScreenStreamingApi
import com.flipperdevices.screenstreaming.impl.fragment.ScreenStreamingFragment
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ScreenStreamingApiImpl @Inject constructor() : ScreenStreamingApi {
    override fun provideScreen(): Screen {
        return FragmentScreen { ScreenStreamingFragment() }
    }
}
