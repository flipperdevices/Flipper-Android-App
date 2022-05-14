package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.composable.LocalRouter
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.SimpleElement
import com.flipperdevices.settings.impl.viewmodels.ExperimentalViewModel

@Composable
fun ColumnScope.ExperimentalCategory(
    settings: Settings,
    experimentalViewModel: ExperimentalViewModel = viewModel()
) {
    val router = LocalRouter.current

    SimpleElement(
        titleId = R.string.experimental_file_manager,
        descriptionId = R.string.experimental_file_manager_desc,
        onClick = { experimentalViewModel.onOpenFileManager(router) }
    )
    SimpleElement(
        titleId = R.string.experimental_screen_streaming,
        descriptionId = R.string.experimental_screen_streaming_desc,
        onClick = { experimentalViewModel.onScreenStreaming(router) }
    )
}
