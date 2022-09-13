package com.flipperdevices.wearable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.wearable.di.WearableComponent
import com.flipperdevices.wearable.setup.api.SetupApi
import com.flipperdevices.wearable.theme.WearFlipperTheme

class MainWearActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val futureEntries by ComponentHolder.component<WearableComponent>().futureEntries
        val composableFutureEntries by ComponentHolder.component<WearableComponent>()
            .composableFutureEntries

        setContent {
            WearFlipperTheme {
                SetUpNavigation(futureEntries, composableFutureEntries)
            }
        }
    }

    @Composable
    private fun SetUpNavigation(
        futureEntries: Set<AggregateFeatureEntry>,
        composableFutureEntries: Set<ComposableFeatureEntry>
    ) {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = SetupApi.ROUTE,
            modifier = Modifier
                .fillMaxSize()
                .background(LocalPallet.current.background)
        ) {
            futureEntries.forEach {
                with(it) {
                    navigation(navController)
                }
            }
            composableFutureEntries.forEach { featureEntry ->
                composable(featureEntry.featureRoute, featureEntry.arguments) { backStack ->
                    with(featureEntry) {
                        Composable(
                            navController = navController,
                            backStackEntry = backStack
                        )
                    }
                }
            }
        }
    }
}
