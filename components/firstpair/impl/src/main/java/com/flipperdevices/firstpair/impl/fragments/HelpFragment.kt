package com.flipperdevices.firstpair.impl.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.firstpair.impl.composable.help.ComposableHelp
import com.flipperdevices.core.ui.res.R as DesignSystem

class HelpFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableHelp(onBack = {
            requireRouter().exit()
        })
    }

    override fun getStatusBarColor() = DesignSystem.color.background
}
