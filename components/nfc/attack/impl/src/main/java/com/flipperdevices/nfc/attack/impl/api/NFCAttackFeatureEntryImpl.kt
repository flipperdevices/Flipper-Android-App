package com.flipperdevices.nfc.attack.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.nfc.attack.api.NFCAttackFeatureEntry
import com.flipperdevices.nfc.attack.impl.composable.ComposableNfcAttack
import com.flipperdevices.nfc.mfkey32.api.MfKey32ScreenEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, NFCAttackFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class NFCAttackFeatureEntryImpl @Inject constructor(
    private val mfKey32ScreenEntry: MfKey32ScreenEntry
) : NFCAttackFeatureEntry {
    override fun start() = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable(start()) {
                ComposableNfcAttack(onOpenMfKey32 = {
                    navController.navigate(mfKey32ScreenEntry.ROUTE.name)
                }, onBack = navController::popBackStack)
            }
        }
    }

}