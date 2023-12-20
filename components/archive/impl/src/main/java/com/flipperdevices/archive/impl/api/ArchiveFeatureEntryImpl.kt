package com.flipperdevices.archive.impl.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.flipperdevices.archive.api.ArchiveDecomposeComponent
import com.flipperdevices.archive.api.ArchiveFeatureEntry
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.ui.decompose.rememberComponentContext
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, ArchiveFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class ArchiveFeatureEntryImpl @Inject constructor(
    private val archiveDecomposeComponentFactory: ArchiveDecomposeComponent.Factory
) : ArchiveFeatureEntry {
    override fun getArchiveScreen(): String {
        return "@${ROUTE.name}"
    }

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(
            route = ROUTE.name,
            startDestination = getArchiveScreen()
        ) {
            composable(
                route = getArchiveScreen()
            ) {
                val componentContext = rememberComponentContext()
                val fileManagerComponent = remember(componentContext) {
                    archiveDecomposeComponentFactory(componentContext) as ArchiveDecomposeComponentImpl
                }
                val childStack by fileManagerComponent.stack.subscribeAsState()

                Children(
                    stack = childStack
                ) {
                    it.instance.Render()
                }
            }
        }
    }
}
