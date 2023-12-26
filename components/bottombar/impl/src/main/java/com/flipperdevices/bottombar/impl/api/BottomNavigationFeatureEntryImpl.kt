package com.flipperdevices.bottombar.impl.api

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.flipperdevices.bottombar.api.BottomNavigationFeatureEntry
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bottombar.model.BottomBarTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.ui.decompose.rememberComponentContext
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, BottomNavigationFeatureEntry::class)
@ContributesBinding(AppGraph::class, BottomNavigationHandleDeeplink::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class BottomNavigationFeatureEntryImpl @Inject constructor(
    private val bottomBarFactory: BottomBarDecomposeComponentImpl.Factory
) : BottomNavigationFeatureEntry, BottomNavigationHandleDeeplink {
    override fun start(): String = "@${ROUTE.name}"
    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(route = start()) {
            val componentContext = rememberComponentContext()
            val component = remember(componentContext) {
                bottomBarFactory(
                    componentContext = componentContext,
                    onBack = { navController.popBackStack() }
                )
            }
            component.Render()
        }
    }

    @Suppress("ForbiddenComment")
    override fun onChangeTab(tab: BottomBarTab, force: Boolean) {
        // TODO: Implement
    }
}
