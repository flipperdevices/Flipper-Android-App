package com.flipperdevices.changelog.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.changelog.api.ChangelogScreenDecomposeComponent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.updater.model.UpdateRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory


@ContributesAssistedFactory(AppGraph::class, ChangelogScreenDecomposeComponent.Factory::class)
class ChangelogScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted updateRequest: UpdateRequest?,
    @Assisted onBack: DecomposeOnBackParameter
): ChangelogScreenDecomposeComponent(componentContext) {
    @Composable
    override fun Render() {

    }
}