package com.flipperdevices.nfc.attack.impl.api

import android.content.Intent
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.nfc.attack.api.NFCAttackFeatureEntry
import com.flipperdevices.nfc.attack.impl.composable.ComposableNfcAttack
import com.flipperdevices.nfc.mfkey32.api.MfKey32HandleDeeplink
import com.flipperdevices.nfc.mfkey32.api.MfKey32ScreenEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, NFCAttackFeatureEntry::class)
@ContributesBinding(AppGraph::class, MfKey32HandleDeeplink::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class NFCAttackFeatureEntryImpl @Inject constructor(
    private val mfKey32ScreenEntry: MfKey32ScreenEntry
) : NFCAttackFeatureEntry, MfKey32HandleDeeplink {

    private var rootNavHostController: NavHostController? = null

    override fun start() = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        rootNavHostController = navController
        navigation(startDestination = start(), route = ROUTE.name) {
            composable(start()) {
                ComposableNfcAttack(onOpenMfKey32 = {
                    navController.navigate(mfKey32ScreenEntry.ROUTE.name)
                }, onBack = navController::popBackStack)
            }
        }
    }

    override fun handleDeepLink(intent: Intent) {
        rootNavHostController?.handleDeepLink(intent)
    }
}
