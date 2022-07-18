package com.flipperdevices.updater.screen.fragments

import android.content.Context
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.fragment.provider.StatusBarColorProvider
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.singleactivity.api.SingleActivityApi
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.screen.composable.ComposableUpdaterScreen
import com.flipperdevices.updater.screen.di.UpdaterComponent
import com.flipperdevices.updater.screen.model.UpdaterScreenState
import com.flipperdevices.updater.screen.viewmodel.FlipperColorViewModel
import com.flipperdevices.updater.screen.viewmodel.UpdaterViewModel
import javax.inject.Inject

private const val EXTRA_UPDATE_REQUEST = "update_request"

class UpdaterFragment : ComposeFragment(), StatusBarColorProvider {
    @Inject
    lateinit var singleActivity: SingleActivityApi

    @Inject
    lateinit var flipperColorViewModel: FlipperColorViewModel

    private val updaterViewModel by viewModels<UpdaterViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ComponentHolder.component<UpdaterComponent>().inject(this)

        val updateRequest = arguments?.getParcelable<UpdateRequest>(EXTRA_UPDATE_REQUEST)
        updaterViewModel.start(updateRequest)
    }

    override fun onStart() {
        super.onStart()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    @Composable
    override fun RenderView() {
        val flipperColor by flipperColorViewModel.getFlipperColor().collectAsState()

        val updaterScreenState by updaterViewModel.getState().collectAsState()
        if (updaterScreenState is UpdaterScreenState.Finish) {
            return
        }

        ComposableUpdaterScreen(updaterScreenState, flipperColor, updaterViewModel::cancel) {
            val updateRequest = arguments?.getParcelable<UpdateRequest>(EXTRA_UPDATE_REQUEST)
            updaterViewModel.retry(updateRequest)
        }
    }

    override fun onStop() {
        super.onStop()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun getStatusBarColor() = DesignSystem.color.background

    companion object {
        fun getInstance(updateRequest: UpdateRequest?): UpdaterFragment {
            return UpdaterFragment().withArgs {
                putParcelable(EXTRA_UPDATE_REQUEST, updateRequest)
            }
        }
    }
}
