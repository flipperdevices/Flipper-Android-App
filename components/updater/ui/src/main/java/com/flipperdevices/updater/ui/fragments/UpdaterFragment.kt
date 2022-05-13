package com.flipperdevices.updater.ui.fragments

import android.content.Context
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.singleactivity.api.SingleActivityApi
import com.flipperdevices.updater.model.VersionFiles
import com.flipperdevices.updater.ui.composable.ComposableUpdaterScreen
import com.flipperdevices.updater.ui.di.UpdaterComponent
import com.flipperdevices.updater.ui.model.UpdaterScreenState
import com.flipperdevices.updater.ui.viewmodel.UpdaterViewModel
import javax.inject.Inject

private const val EXTRA_VERSION_FILES = "version_files"

class UpdaterFragment : ComposeFragment() {
    @Inject
    lateinit var singleActivity: SingleActivityApi

    private val updaterViewModel by viewModels<UpdaterViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ComponentHolder.component<UpdaterComponent>().inject(this)

        val versionFiles = arguments?.getParcelable<VersionFiles>(EXTRA_VERSION_FILES)
        updaterViewModel.start(versionFiles)
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
        fun getInstance(versionFiles: VersionFiles?): UpdaterFragment {
            return UpdaterFragment().withArgs {
                putParcelable(EXTRA_VERSION_FILES, versionFiles)
            }
        }
    }
}
