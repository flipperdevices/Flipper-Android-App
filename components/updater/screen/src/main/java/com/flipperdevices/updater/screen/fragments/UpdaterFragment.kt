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
import com.flipperdevices.singleactivity.api.SingleActivityApi
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.screen.composable.ComposableUpdaterScreen
import com.flipperdevices.updater.screen.di.UpdaterComponent
import com.flipperdevices.updater.screen.model.UpdaterScreenState
import com.flipperdevices.updater.screen.viewmodel.UpdaterViewModel
import javax.inject.Inject

private const val EXTRA_UPDATE_REQUEST = "update_request"

class UpdaterFragment : ComposeFragment() {
    @Inject
    lateinit var singleActivity: SingleActivityApi

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
        val updaterScreenState by updaterViewModel.getState().collectAsState()
        if (updaterScreenState is UpdaterScreenState.Finish) {
            onFinish()
            return
        }
        ComposableUpdaterScreen(updaterScreenState, updaterViewModel)
    }

    override fun onStop() {
        super.onStop()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun onFinish() {
        singleActivity.open()
    }

    companion object {
        fun getInstance(updateRequest: UpdateRequest?): UpdaterFragment {
            return UpdaterFragment().withArgs {
                putParcelable(EXTRA_UPDATE_REQUEST, updateRequest)
            }
        }
    }
}
