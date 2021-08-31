package com.flipper.pair.navigation

import com.flipper.core.di.AppGraph
import com.flipper.pair.find.FindDeviceFragment
import com.flipper.pair.permission.PermissionFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class PairNavigationScreensImpl @Inject constructor() : PairNavigationScreens {
    override fun permissionScreen() = FragmentScreen { PermissionFragment() }
    override fun findDeviceScreen() = FragmentScreen { FindDeviceFragment() }
}