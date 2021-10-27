package com.flipperdevices.pair.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.pair.impl.PairScreenActivity
import com.flipperdevices.pair.impl.findstandart.StandartFindFragment
import com.flipperdevices.pair.impl.findstandart.service.PairDeviceViewModel
import com.flipperdevices.pair.impl.fragments.findcompanion.CompanionFindFragment
import com.flipperdevices.pair.impl.fragments.guide.FragmentGuide
import com.flipperdevices.pair.impl.fragments.permission.PermissionFragment
import com.flipperdevices.pair.impl.fragments.tos.FragmentTOS
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
    fun inject(viewModel: PairDeviceViewModel)
}
