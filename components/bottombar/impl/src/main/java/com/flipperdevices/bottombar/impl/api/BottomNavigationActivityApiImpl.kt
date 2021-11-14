package com.flipperdevices.bottombar.impl.api

import com.flipperdevices.bottombar.api.BottomNavigationActivityApi
import com.flipperdevices.bottombar.impl.main.BottomNavigationFragment
import com.flipperdevices.core.di.AppGraph
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class BottomNavigationActivityApiImpl @Inject constructor() : BottomNavigationActivityApi {
    override fun getBottomNavigationFragment(): FragmentScreen {
        return FragmentScreen { BottomNavigationFragment() }
    }
}
