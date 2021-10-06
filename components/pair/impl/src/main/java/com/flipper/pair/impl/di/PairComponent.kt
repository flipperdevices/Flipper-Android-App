package com.flipper.pair.impl.di

import com.flipper.core.di.AppGraph
import com.flipper.pair.impl.PairScreenActivity
import com.flipper.pair.impl.findcompanion.CompanionFindFragment
import com.flipper.pair.impl.findstandart.StandartFindFragment
import com.flipper.pair.impl.fragments.guide.FragmentGuide
import com.flipper.pair.impl.fragments.tos.FragmentTOS
import com.flipper.pair.impl.permission.PermissionFragment
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Singleton

@Singleton
@ContributesTo(AppGraph::class)
interface PairComponent {
    fun inject(fragment: PermissionFragment)
    fun inject(fragment: StandartFindFragment)
    fun inject(fragment: FragmentTOS)
    fun inject(fragment: FragmentGuide)
    fun inject(fragment: CompanionFindFragment)
    fun inject(activity: PairScreenActivity)
}
