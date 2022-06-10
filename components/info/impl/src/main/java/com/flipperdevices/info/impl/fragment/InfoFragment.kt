package com.flipperdevices.info.impl.fragment

import com.flipperdevices.core.ui.res.R as DesignSystem
import android.os.Bundle
import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.info.impl.compose.ComposableDeviceInfoScreen
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.api.UpdaterUIApi
import javax.inject.Inject

class InfoFragment : ComposeFragment() {
    @Inject
    lateinit var updaterUiApi: UpdaterUIApi

    @Inject
    lateinit var updaterApi: UpdaterApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<InfoComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        ComposableDeviceInfoScreen(updaterApi, updaterUiApi)
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.accent
}
