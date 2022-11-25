package com.flipperdevices.hub.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.faphub.maincard.api.MainCardApi
import com.flipperdevices.hub.impl.composable.ComposableHub
import com.flipperdevices.nfc.attack.api.NFCAttackFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, HubFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class HubFeatureEntryImpl @Inject constructor(
    private val nfcAttackFeatureEntry: NFCAttackFeatureEntry,
    private val mainCardApi: MainCardApi
) : HubFeatureEntry {
    override fun start() = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(
            startDestination = start(),
            route = ROUTE.name
        ) {
            composable(start()) {
                ComposableHub(
                    mainCardApi = mainCardApi,
                    onOpenAttack = {
                        navController.navigate(nfcAttackFeatureEntry.ROUTE.name)
                    }, onOpenFapHub = {
                    }
                )
            }
        }
    }
}
