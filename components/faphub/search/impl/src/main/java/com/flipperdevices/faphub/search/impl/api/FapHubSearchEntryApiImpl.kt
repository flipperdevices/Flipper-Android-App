package com.flipperdevices.faphub.search.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.fapscreen.api.FapScreenApi
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.api.toFapButtonConfig
import com.flipperdevices.faphub.search.api.FapHubSearchEntryApi
import com.flipperdevices.faphub.search.impl.composable.ComposableSearchScreen
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, FapHubSearchEntryApi::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class FapHubSearchEntryApiImpl @Inject constructor(
    fapScreenApiProvider: Provider<FapScreenApi>,
    private val fapInstallationUIApi: FapInstallationUIApi,
    private val errorsRenderer: FapHubComposableErrorsRenderer
) : FapHubSearchEntryApi {
    private val fapScreenApi by fapScreenApiProvider

    override fun start() = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable("@${ROUTE.name}") {
                ComposableSearchScreen(
                    onBack = { navController.popBackStack() },
                    onFapItemClick = {
                        navController.navigate(fapScreenApi.getFapScreen(it.id))
                    },
                    errorsRenderer = errorsRenderer,
                    installationButton = { fapItem, modifier ->
                        fapInstallationUIApi.ComposableButton(
                            config = fapItem?.toFapButtonConfig(),
                            modifier = modifier,
                            fapButtonSize = FapButtonSize.COMPACTED
                        )
                    }
                )
            }
        }
    }
}
