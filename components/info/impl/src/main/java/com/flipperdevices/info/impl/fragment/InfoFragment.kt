package com.flipperdevices.info.impl.fragment

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.info.impl.compose.ComposableDeviceInfoScreen

class InfoFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableDeviceInfoScreen()
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.accent
}
