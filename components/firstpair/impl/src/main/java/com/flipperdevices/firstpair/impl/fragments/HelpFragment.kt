package com.flipperdevices.firstpair.impl.fragments

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.runtime.Composable
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.firstpair.impl.composable.help.ComposableHelp

class HelpFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableHelp(onBack = {
            requireRouter().exit()
        })
    }

    override fun getStatusBarColor() = DesignSystem.color.background
}
