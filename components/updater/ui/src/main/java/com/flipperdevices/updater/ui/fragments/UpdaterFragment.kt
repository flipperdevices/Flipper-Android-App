package com.flipperdevices.updater.ui.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.updater.model.VersionFiles

private const val EXTRA_VERSION_FILES = "version_files"

class UpdaterFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
    }

    companion object {
        fun getInstance(versionFiles: VersionFiles?): UpdaterFragment {
            return UpdaterFragment().withArgs {
                putParcelable(EXTRA_VERSION_FILES, versionFiles)
            }
        }
    }
}
