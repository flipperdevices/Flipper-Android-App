package com.flipperdevices.changelog.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.changelog.api.ChangelogFormatterApi
import com.flipperdevices.changelog.api.ChangelogScreenDecomposeComponent
import com.flipperdevices.changelog.impl.composable.ChangelogScreenComposable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.updater.model.UpdateRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory


@ContributesAssistedFactory(AppGraph::class, ChangelogScreenDecomposeComponent.Factory::class)
class ChangelogScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val updateRequest: UpdateRequest,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val changelogFormatter: ChangelogFormatterApi
) : ChangelogScreenDecomposeComponent(componentContext) {
    @Composable
    override fun Render() {
        val changelog = updateRequest.changelog
        ChangelogScreenComposable(
            updateRequest = updateRequest,
            changelog = remember(changelog) {
                if (changelog != null) {
                    changelogFormatter.format(changelog)
                } else null
            },
            onBack = onBack::invoke
        )
    }
}