package com.flipperdevices.firstpair.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.firstpair.impl.fragments.DeviceSearchingFragment
import com.flipperdevices.firstpair.impl.fragments.TermsOfServiceFragment
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.PairDeviceViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface FirstPairComponent {
    fun inject(fragment: TermsOfServiceFragment)
    fun inject(fragment: DeviceSearchingFragment)
    fun inject(viewModel: PairDeviceViewModel)
    fun inject(viewModel: BLEDeviceViewModel)
}
