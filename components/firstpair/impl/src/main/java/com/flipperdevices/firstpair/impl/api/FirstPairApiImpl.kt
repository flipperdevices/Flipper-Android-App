package com.flipperdevices.firstpair.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.firstpair.impl.fragments.DeviceSearchingFragment
import com.flipperdevices.firstpair.impl.fragments.TermsOfServiceFragment
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class FirstPairApiImpl @Inject constructor(
    private val firstPairStorage: FirstPairStorage
) : FirstPairApi {
    override fun getFirstPairScreen(): Screen {
        return FragmentScreen {
            if (firstPairStorage.isTosPassed()) {
                DeviceSearchingFragment()
            } else {
                TermsOfServiceFragment()
            }
        }
    }

    override fun shouldWeOpenPairScreen(): Boolean {
        return firstPairStorage.isTosPassed().not() || firstPairStorage.isDeviceSelected().not()
    }
}
