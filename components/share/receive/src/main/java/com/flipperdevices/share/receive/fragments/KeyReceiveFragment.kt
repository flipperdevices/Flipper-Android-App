package com.flipperdevices.share.receive.fragments

import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.receive.composable.ComposableKeyReceive
import com.flipperdevices.share.receive.di.KeyReceiveComponent
import javax.inject.Inject

internal const val EXTRA_KEY_DEEPLINK = "deeplink"

class KeyReceiveFragment : ComposeFragment() {

    @Inject
    lateinit var keyScreenApi: KeyScreenApi

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    init {
        ComponentHolder.component<KeyReceiveComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        ComposableKeyReceive(keyScreenApi = keyScreenApi)
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.background

    companion object {
        fun newInstance(deeplink: Deeplink): Fragment {
            return KeyReceiveFragment().withArgs {
                putParcelable(EXTRA_KEY_DEEPLINK, deeplink)
            }
        }
    }
}
