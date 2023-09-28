package com.flipperdevices.nfc.mfkey32.screen.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.nfc.mfkey32.api.MfKey32ScreenEntry
import com.flipperdevices.nfc.mfkey32.screen.composable.ComposableMfKey32Screen
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

private const val DEEPLINK_SCHEME = DeeplinkConstants.SCHEMA
private const val DEEPLINK_MF_KEY = "${DEEPLINK_SCHEME}/mfkey32"

@ContributesBinding(AppGraph::class, MfKey32ScreenEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class MfKey32ScreenEntryImpl @Inject constructor() : MfKey32ScreenEntry {
    override fun startDestination() = "@${ROUTE.name}"
    override fun getMfKeyScreenByDeeplink(): String {
        return DEEPLINK_MF_KEY
    }

    private val deeplinkArguments = listOf(
        navDeepLink {
            uriPattern = DEEPLINK_MF_KEY
        }
    )

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(
            startDestination = startDestination(),
            route = ROUTE.name
        ) {
            composable(
                route = startDestination(),
                deepLinks = deeplinkArguments
            ) {
                ComposableMfKey32Screen(navController)
            }
        }
    }
}
