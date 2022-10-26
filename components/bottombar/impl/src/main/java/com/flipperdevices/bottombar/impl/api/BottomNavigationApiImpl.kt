package com.flipperdevices.bottombar.impl.api

import com.flipperdevices.bottombar.api.BottomNavigationApi
import com.flipperdevices.bottombar.impl.main.BottomNavigationFragment
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class BottomNavigationApiImpl @Inject constructor() : BottomNavigationApi {
    override fun getBottomNavigationFragment(deeplink: Deeplink?): FragmentScreen {
        return FragmentScreen { BottomNavigationFragment.newInstance(deeplink) }
    }
}
