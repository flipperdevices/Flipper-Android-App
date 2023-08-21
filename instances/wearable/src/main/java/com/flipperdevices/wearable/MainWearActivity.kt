package com.flipperdevices.wearable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.wearable.di.WearableComponent
import com.flipperdevices.wearable.emulate.api.ChannelClientHelper
import com.flipperdevices.wearable.setup.api.SetupApi
import com.flipperdevices.wearable.theme.WearFlipperTheme
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import javax.inject.Inject

class MainWearActivity : ComponentActivity() {

    @Inject
    lateinit var channelClientHelper: ChannelClientHelper

    private val channelClient: ChannelClient by lazy { Wearable.getChannelClient(this) }

    private val channelClientCallback = object : ChannelClient.ChannelCallback() {
        override fun onChannelClosed(
            channel: ChannelClient.Channel,
            closeReason: Int,
            appSpecificErrorCode: Int
        ) {
            super.onChannelClosed(channel, closeReason, appSpecificErrorCode)
            channelClientHelper.onChannelCloseFromPhone(lifecycleScope)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<WearableComponent>().inject(this)
        channelClient.registerChannelCallback(channelClientCallback)
        channelClientHelper.onChannelOpen(lifecycleScope)

        val futureEntries by ComponentHolder.component<WearableComponent>().futureEntries
        val composableFutureEntries by ComponentHolder.component<WearableComponent>()
            .composableFutureEntries
        val setupApi = ComponentHolder.component<WearableComponent>().setupApi

        setContent {
            WearFlipperTheme {
                SetUpNavigation(
                    futureEntries.toImmutableSet(),
                    composableFutureEntries.toImmutableSet(),
                    setupApi
                )
            }
        }
    }

    @Composable
    private fun SetUpNavigation(
        futureEntries: ImmutableSet<AggregateFeatureEntry>,
        composableFutureEntries: ImmutableSet<ComposableFeatureEntry>,
        setupApi: SetupApi
    ) {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = setupApi.ROUTE.name,
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
                with(featureEntry) {
                    composable(navController)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        print("onDestroy")
        channelClient.unregisterChannelCallback(channelClientCallback)
        channelClientHelper.onDestroy()
    }
}
