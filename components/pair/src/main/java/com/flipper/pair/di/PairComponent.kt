package com.flipper.pair.di

import com.flipper.core.di.AppGraph
import com.flipper.pair.find.FindDeviceFragment
import com.flipper.pair.permission.PermissionFragment
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface PairComponent {
    fun inject(fragment: PermissionFragment)
    fun inject(fragment: FindDeviceFragment)
}