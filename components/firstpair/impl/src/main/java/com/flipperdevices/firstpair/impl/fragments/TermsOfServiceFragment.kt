package com.flipperdevices.firstpair.impl.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.firstpair.impl.composable.tos.ComposableTOS
import com.flipperdevices.firstpair.impl.di.FirstPairComponent
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import com.flipperdevices.singleactivity.api.SingleActivityApi
import com.github.terrakok.cicerone.androidx.FragmentScreen
import javax.inject.Inject

class TermsOfServiceFragment : ComposeFragment() {
    @Inject
    lateinit var firstPairStorage: FirstPairStorage

    @Inject
    lateinit var singleActivityApi: SingleActivityApi

    init {
        ComponentHolder.component<FirstPairComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        ComposableTOS(onApplyPress = this::onTosApply)
    }

    override fun getStatusBarColor() = DesignSystem.color.background

    private fun onTosApply() {
        firstPairStorage.markTosPassed()
        if (firstPairStorage.isDeviceSelected()) {
            singleActivityApi.open()
        } else {
            requireRouter().navigateTo(
                FragmentScreen {
                    DeviceSearchingFragment()
                }
            )
        }
    }
}
