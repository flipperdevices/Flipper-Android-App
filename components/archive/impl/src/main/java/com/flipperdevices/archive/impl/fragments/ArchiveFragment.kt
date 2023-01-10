package com.flipperdevices.archive.impl.fragments

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.flipperdevices.archive.impl.composable.ComposableArchive
import com.flipperdevices.archive.impl.di.ArchiveComponent
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.fragment.ComposeFragment
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

class ArchiveFragment : ComposeFragment() {
    @Inject
    lateinit var connectionApi: ConnectionApi

    @Inject
    lateinit var synchronizationUiApi: SynchronizationUiApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<ArchiveComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        ComposableArchive(synchronizationUiApi)
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.accent
}
