package com.flipperdevices.settings.impl.composable.category

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.composable.LocalRouter
import com.flipperdevices.settings.impl.R
import com.flipperdevices.settings.impl.composable.elements.SimpleElement
import com.flipperdevices.settings.impl.viewmodels.DebugViewModel

@Composable
fun ColumnScope.DebugCategory(
    debugViewModel: DebugViewModel = viewModel()
) {
    val router = LocalRouter.current

    SimpleElement(
        titleId = R.string.debug_stress_test,
        onClick = { debugViewModel.onOpenStressTest(router) }
    )
    SimpleElement(
        titleId = R.string.debug_start_synchronization,
        onClick = { debugViewModel.onStartSynchronization() }
    )
    SimpleElement(
        titleId = R.string.debug_connection_to_another,
        onClick = { debugViewModel.onOpenConnectionScreen() }
    )
}
