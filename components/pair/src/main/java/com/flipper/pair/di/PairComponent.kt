package com.flipper.pair.di

import com.flipper.core.di.AppGraph
import com.flipper.pair.PairScreenActivity
import com.flipper.pair.findcompanion.CompanionFindFragment
import com.flipper.pair.findstandart.StandartFindFragment
import com.flipper.pair.permission.PermissionFragment
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface PairComponent {
    fun inject(fragment: PermissionFragment)
    fun inject(fragment: StandartFindFragment)
    fun inject(fragment: CompanionFindFragment)
    fun inject(activity: PairScreenActivity)
}
