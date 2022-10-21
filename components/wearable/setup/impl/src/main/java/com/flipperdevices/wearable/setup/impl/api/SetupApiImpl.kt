package com.flipperdevices.wearable.setup.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import androidx.wear.compose.navigation.composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.wearable.setup.api.SetupApi
import com.flipperdevices.wearable.setup.impl.composable.ComposableFindPhone
import com.flipperdevices.wearable.sync.wear.api.KeysListApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SetupApi::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class SetupApiImpl @Inject constructor(
    private val keysListApi: KeysListApi
) : SetupApi {
    override fun start() = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable(start()) {
                ComposableFindPhone(onFoundPhone = {
                    navController.navigate(keysListApi.start()) {
                        launchSingleTop = true
                        popUpTo(0)
                    }
                })
            }
        }
    }
}
