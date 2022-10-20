package com.flipperdevices.nfc.mfkey32.screen.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.nfc.mfkey32.api.MfKey32ScreenEntry
import com.flipperdevices.nfc.mfkey32.screen.composable.ComposableMfKey32Screen
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject


@ContributesBinding(AppGraph::class, MfKey32ScreenEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class MfKey32ScreenApiImpl @Inject constructor() : MfKey32ScreenEntry {
    override fun startDestination() = "@${ROUTE.name}"
    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(
            startDestination = startDestination(),
            route = ROUTE.name
        ) {
            composable(startDestination()) {
                ComposableMfKey32Screen(navController)
            }
        }
    }
}
