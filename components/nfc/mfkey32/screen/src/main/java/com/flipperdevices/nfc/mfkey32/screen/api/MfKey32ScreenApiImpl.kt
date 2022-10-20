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

private const val ROUTE = "@mfkey32"
private const val MAIN_SCREEN_ROUTE = "mfkey32"

@ContributesBinding(AppGraph::class, MfKey32ScreenEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class MfKey32ScreenApiImpl @Inject constructor() : MfKey32ScreenEntry {
    override fun startDestination() = MAIN_SCREEN_ROUTE
    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = startDestination(), route = ROUTE) {
            composable(MAIN_SCREEN_ROUTE) {
                ComposableMfKey32Screen(navController)
            }
        }
    }
}
