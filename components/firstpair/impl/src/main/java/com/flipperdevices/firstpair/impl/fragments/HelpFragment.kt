package com.flipperdevices.firstpair.impl.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.firstpair.impl.composable.help.ComposableHelp

class HelpFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableHelp(onBack = {
            requireRouter().exit()
        })
    }
}
