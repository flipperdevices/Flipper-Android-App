package com.flipperdevices.keyedit.impl.fragment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.keyedit.impl.composable.ComposableEditScreen
import com.flipperdevices.keyedit.impl.model.EditableKey
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel
import tangle.viewmodel.fragment.tangleViewModel

const val EXTRA_EDITABLE_KEY = "editable_key"
const val EXTRA_TITLE_KEY = "title_key"

class KeyEditFragment : ComposeFragment() {
    private val viewModel by tangleViewModel<KeyEditViewModel>()

    @Composable
    override fun RenderView() {
        val router = LocalRouter.current
        val state by viewModel.getEditState().collectAsState()
        val title = remember {
            arguments?.getString(EXTRA_TITLE_KEY)
        }
        ComposableEditScreen(
            viewModel,
            title = title,
            state = state,
            onBack = router::exit,
            onSave = { viewModel.onSave { router.exit() } }
        )
    }

    companion object {
        fun getInstance(editableKey: EditableKey, title: String?): KeyEditFragment {
            return KeyEditFragment().withArgs {
                putParcelable(EXTRA_EDITABLE_KEY, editableKey)
                putString(EXTRA_TITLE_KEY, title)
            }
        }
    }
}
