package com.flipperdevices.faphub.fapscreen.impl.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.faphub.fapscreen.api.FapScreenApi
import com.flipperdevices.faphub.fapscreen.impl.composable.ComposableFapScreen
import com.flipperdevices.faphub.installation.api.FapInstallationState
import com.flipperdevices.faphub.installation.api.FapInstallationUIApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

internal const val FAP_ID_KEY = "fap_id"

@ContributesBinding(AppGraph::class, FapScreenApi::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class FapScreenApiImpl @Inject constructor(
    private val installationUIApi: FapInstallationUIApi
) : FapScreenApi {
    private val fapArguments = listOf(
        navArgument(FAP_ID_KEY) {
            type = NavType.Companion.StringType
            nullable = false
        }
    )

    override fun getFapScreen(
        id: String
    ) = "@${ROUTE.name}?fap_id=${Uri.encode(id)}"

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}?fap_id={$FAP_ID_KEY}",
            fapArguments
        ) {
            ComposableFapScreen(
                onBack = navController::popBackStack,
                installationButton = { fapItem, modifier, fontSize ->
                    installationUIApi.ComposableButton(
                        fapItem = fapItem,
                        modifier = modifier,
                        textSize = fontSize,
                        state = FapInstallationState.getRandomState()
                    )
                }
            )
        }
    }
}
