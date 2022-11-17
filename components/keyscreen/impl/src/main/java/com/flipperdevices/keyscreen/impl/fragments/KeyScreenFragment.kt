package com.flipperdevices.keyscreen.impl.fragments

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.keyscreen.api.KeyEmulateApi
import com.flipperdevices.keyscreen.impl.composable.ComposableKeyScreen
import com.flipperdevices.keyscreen.impl.composable.card.KeyScreenNavigation
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModelFactory
import com.flipperdevices.nfceditor.api.NfcEditorApi
import com.flipperdevices.share.api.ShareBottomFeatureEntry
import javax.inject.Inject

private const val EXTRA_KEY_PATH = "flipper_key_path"

class KeyScreenFragment : ComposeFragment() {

    @Inject
    lateinit var synchronizationUiApi: SynchronizationUiApi

    @Inject
    lateinit var nfcEditor: NfcEditorApi

    @Inject
    lateinit var keyEmulateApi: KeyEmulateApi

    @Inject
    lateinit var shareBottomSheetApi: ShareBottomFeatureEntry

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
    }

    private val flipperKeyPath: FlipperKeyPath?
        get() = arguments?.getParcelable(EXTRA_KEY_PATH)

    private val viewModel by viewModels<KeyScreenViewModel> {
        KeyScreenViewModelFactory(
            keyPath = flipperKeyPath,
            requireActivity().application
        )
    }

    @Composable
    override fun RenderView() {
        KeyScreenNavigation(
            shareBottomSheetApi,
            screenContent = { onShare ->
                ComposableKeyScreen(
                    viewModel,
                    synchronizationUiApi,
                    nfcEditor,
                    keyEmulateApi,
                    onShare = { onShare(flipperKeyPath) }
                )
            }
        )
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.background

    companion object {
        fun getInstance(keyPath: FlipperKeyPath) = KeyScreenFragment().withArgs {
            putParcelable(EXTRA_KEY_PATH, keyPath)
        }
    }
}
