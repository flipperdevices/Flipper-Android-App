package com.flipperdevices.updater.screen.fragments

import android.content.Context
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.viewModels
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.parcelable
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.fragment.provider.StatusBarColorProvider
import com.flipperdevices.singleactivity.api.SingleActivityApi
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.screen.composable.ComposableCancelDialog
import com.flipperdevices.updater.screen.composable.ComposableUpdaterScreen
import com.flipperdevices.updater.screen.di.UpdaterComponent
import com.flipperdevices.updater.screen.model.UpdaterScreenState
import com.flipperdevices.updater.screen.viewmodel.FlipperColorViewModel
import com.flipperdevices.updater.screen.viewmodel.UpdaterViewModel
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val EXTRA_UPDATE_REQUEST = "update_request"

class UpdaterFragment : ComposeFragment(), StatusBarColorProvider {
    @Inject
    lateinit var singleActivity: SingleActivityApi

    private val flipperColorViewModel by viewModels<FlipperColorViewModel>()
    private val updaterViewModel by viewModels<UpdaterViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ComponentHolder.component<UpdaterComponent>().inject(this)

        val updateRequest = arguments?.parcelable<UpdateRequest>(EXTRA_UPDATE_REQUEST)
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

        val onAbortUpdate = updaterViewModel::cancelUpdate
        var isCancelDialogOpen by remember { mutableStateOf(false) }
        ComposableUpdaterScreen(
            updaterScreenState = updaterScreenState,
            flipperColor = flipperColor,
            onCancel = { isCancelDialogOpen = true },
            onRetry = {
                val updateRequest = arguments?.parcelable<UpdateRequest>(EXTRA_UPDATE_REQUEST)
                updaterViewModel.retry(updateRequest)
            }
        )
        if (isCancelDialogOpen) {
            when (updaterScreenState) {
                is UpdaterScreenState.Failed -> onAbortUpdate()
                else -> {
                    ComposableCancelDialog(
                        onAbort = onAbortUpdate,
                        onContinue = { isCancelDialogOpen = false }
                    )
                }
            }
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
