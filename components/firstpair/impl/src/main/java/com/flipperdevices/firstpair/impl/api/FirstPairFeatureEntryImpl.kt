package com.flipperdevices.firstpair.impl.api

import ComposableSearchingView
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.firstpair.api.FirstPairFeatureEntry
import com.flipperdevices.firstpair.impl.composable.help.ComposableHelp
import com.flipperdevices.firstpair.impl.composable.tos.ComposableTOS
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import com.flipperdevices.singleactivity.api.SingleActivityApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FirstPairFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class FirstPairFeatureEntryImpl @Inject constructor(
    private val firstPairStorage: FirstPairStorage,
    private val singleActivityApi: SingleActivityApi
) : FirstPairFeatureEntry {
    override fun getFirstPairScreen(): String {
        return if (firstPairStorage.isTosPassed()) {
            getDeviceScreen()
        } else {
            getTOSScreen()
        }
    }

    private fun getDeviceScreen() = "@${ROUTE.name}device"
    private fun getTOSScreen() = "@${ROUTE.name}tos"
    private fun getHelpScreen() = "@${ROUTE.name}help"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(
            startDestination = getFirstPairScreen(),
            route = ROUTE.name
        ) {
            composable(route = getTOSScreen()) {
                ComposableTOS(
                    onApplyPress = {
                        firstPairStorage.markTosPassed()
                        if (firstPairStorage.isDeviceSelected()) {
                            singleActivityApi.open()
                        } else {
                            navController.navigate(route = getDeviceScreen())
                        }
                    }
                )
            }
            composable(route = getDeviceScreen()) {
                val context = LocalContext.current
                ComposableSearchingView(
                    onHelpClicking = {
                        navController.navigate(route = getHelpScreen())
                    },
                    onFinishConnection = singleActivityApi::open,
                    onBack = {
                        if (navController.popBackStack().not()) {
                            (context as? Activity)?.finish()
                        }
                    }
                )
            }
            composable(route = getHelpScreen()) {
                ComposableHelp(onBack = navController::popBackStack)
            }
        }
    }
}
