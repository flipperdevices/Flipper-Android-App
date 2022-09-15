package com.flipperdevices.share.receive.fragments

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.receive.composable.ComposableKeyReceive
import com.flipperdevices.share.receive.di.KeyReceiveComponent
import com.flipperdevices.share.receive.model.ReceiveState
import com.flipperdevices.share.receive.viewmodels.KeyReceiveViewModel
import com.flipperdevices.share.receive.viewmodels.KeyReceiveViewModelFactory
import javax.inject.Inject

private const val EXTRA_KEY_DEEPLINK = "deeplink"

class KeyReceiveFragment : ComposeFragment() {
    private val deeplink: Deeplink?
        get() = arguments?.get(EXTRA_KEY_DEEPLINK) as? Deeplink

    @Inject
    lateinit var keyScreenApi: KeyScreenApi

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    init {
        ComponentHolder.component<KeyReceiveComponent>().inject(this)
    }

    private val receiveViewModel by viewModels<KeyReceiveViewModel> {
        KeyReceiveViewModelFactory(deeplink, requireContext().applicationContext as Application)
    }

    @Composable
    override fun RenderView() {
        val state by receiveViewModel.getState().collectAsState()

        if (state is ReceiveState.Finished) {
            finish()
            return
        }

        ComposableKeyReceive(
            keyScreenApi,
            state,
            receiveViewModel,
            onCancel = {
                requireRouter().exit()
            }
        )
    }

    private fun finish() {
        synchronizationApi.startSynchronization(force = true)
        requireRouter().exit()
    }

    companion object {
        fun newInstance(deeplink: Deeplink): Fragment {
            return KeyReceiveFragment().withArgs {
                putParcelable(EXTRA_KEY_DEEPLINK, deeplink)
            }
        }
    }
}
