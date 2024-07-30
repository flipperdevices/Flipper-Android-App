package com.flipperdevices.changelog.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.changelog.api.ChangelogFormatterApi
import com.flipperdevices.changelog.api.ChangelogScreenDecomposeComponent
import com.flipperdevices.changelog.impl.composable.ChangelogScreenComposable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.updater.model.UpdateRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, ChangelogScreenDecomposeComponent.Factory::class)
class ChangelogScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val updateRequest: UpdateRequest,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val changelogFormatter: ChangelogFormatterApi,
    private val dataStore: DataStore<Settings>
) : ChangelogScreenDecomposeComponent(componentContext) {
    @Composable
    override fun Render() {
        val changelog = updateRequest.changelog
        ChangelogScreenComposable(
            updateRequest = updateRequest,
            changelog = remember(changelog) {
                if (changelog != null) {
                    changelogFormatter.format(changelog)
                } else {
                    null
                }
            },
            onBack = onBack::invoke
        )
    }

    override fun isStatusBarIconLight(systemIsDark: Boolean): Boolean {
        val settings = runBlocking { dataStore.data.first() }
        return when (settings.selected_theme) {
            SelectedTheme.SYSTEM,
            is SelectedTheme.Unrecognized -> systemIsDark
            SelectedTheme.DARK -> true
            SelectedTheme.LIGHT -> false
        }
    }
}
